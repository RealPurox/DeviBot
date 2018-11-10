package me.purox.devi.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.purox.devi.Logger;
import me.purox.devi.core.agents.Agent;
import me.purox.devi.core.agents.AgentManager;
import me.purox.devi.core.waiter.ResponseWaiter;
import me.purox.devi.entities.AnimatedEmote;
import me.purox.devi.entities.Language;
import me.purox.devi.entities.ModuleType;
import me.purox.devi.listener.*;
import me.purox.devi.commands.CommandHandler;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.database.DatabaseManager;
import me.purox.devi.music.MusicManager;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import me.purox.devi.utils.*;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.jodah.expiringmap.ExpiringMap;
import okhttp3.OkHttpClient;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Devi {

    public static final Gson GSON = new GsonBuilder().create();

    private ScheduledExecutorService threadPool;

    private Settings settings;
    private MusicManager musicManager;
    private DatabaseManager databaseManager;
    private CommandHandler commandHandler;
    private ShardManager shardManager;
    private ResponseWaiter responseWaiter;
    private AnimatedEmote animatedEmotes;
    private AgentManager agentManager;

    private ExpiringMap<String, String> prunedMessages = ExpiringMap.builder().variableExpiration().build();
    private List<String> admins = new ArrayList<>();
    private LoadingCache<String, DeviGuild> deviGuildLoadingCache;
    private HashMap<Language, HashMap<Integer, String>> deviTranslations = new HashMap<>();
    private HashMap<String, List<String>> streams = new HashMap<>();
    private List<ModuleType> disabledModules = new ArrayList<>();
    private List<String> voters = new ArrayList<>();

    private OkHttpClient okHttpClient;
    private Jedis redisSender;
    private Logger logger;

    private int songsPlayed;
    private int commandsExecuted;
    private boolean redisConnection = false;

    private Date rebootTime;

    public Devi() {
        this.threadPool = Executors.newSingleThreadScheduledExecutor();

        // init handlers / managers / settings / utils
        this.commandHandler = new CommandHandler(this);
        this.settings = new Settings();
        this.musicManager = new MusicManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.logger = new Logger(this);
        this.okHttpClient = new OkHttpClient();
        this.responseWaiter = new ResponseWaiter();
        new MessageUtils(this);
        this.animatedEmotes = new AnimatedEmote(this);
        this.agentManager = new AgentManager(this);


        songsPlayed = 0;
        commandsExecuted = 0;

        // create cache loader.
        this.deviGuildLoadingCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(new CacheLoader<String, DeviGuild>() {
                    @Override
                    public DeviGuild load(String s) {
                        return createDeviGuild(s);
                    }
                });

        // register languages
        for (Language language : Language.values()) {
            deviTranslations.put(language, new HashMap<>());
        }
    }

    public void boot(String[] args) {
        if (Arrays.asList(args).contains("--devi")) {
            this.settings.disableDevBot();
            logger.log("Starting as public bot");
        } else logger.log("Starting as dev bot");

        // connect to database
        databaseManager.connect();
        // load translations
        loadTranslations();
        // load streams
        loadStreams();
        // reboot everyday at 4:30am
        initDailyReboot();

        try {
            // subscribe to redis channel async because it's blocking the current thread
            Thread redisThread = new Thread(() -> {
                try {
                    redisSender = new Jedis("54.38.182.128");
                    redisSender.auth(settings.getDeviAPIAuthorization());

                    Jedis receiverRedis = new Jedis("54.38.182.128");
                    receiverRedis.auth(settings.getDeviAPIAuthorization());
                    redisConnection = true;
                    receiverRedis.subscribe(getJedisPubSub(), "devi_update", "devi_twitch_event");
                } catch (JedisDataException e) {
                    redisConnection = false;
                    e.printStackTrace();
                }
            });
            redisThread.setName("Devi Redis Thread");
            redisThread.start();

            // block current thread for 1 second to make sure redis is connected
            Thread.sleep(1000);

            // subscribe to twitch events
            Set<String> subscribeToStream = new HashSet<>();
            for (String streamID : streams.keySet()) {
                if (!redisConnection) break;
                //get time of last subscription
                String lastSubscription = getRedisSender().hget("streams#2", streamID);
                //there is no record of a last subscription, subscribe to stream events.
                if (lastSubscription == null) {
                    subscribeToStream.add(streamID);
                    continue;
                }
                //convert to long
                Long lastSubLong = Long.parseLong(lastSubscription);
                //get days
                long days = ((System.currentTimeMillis() - lastSubLong) / (60*60*24*1000));
                //the subscription was more than 5 days ago, renew the subscription.
                if (days >= 5) {
                    logger.log("Twitch event subscription to stream id " + streamID + " was more than 5 days ago, renewing ...");
                    subscribeToStream.add(streamID);
                }
            }
            //(re-)sub to them
            changeTwitchSubscriptionStatus(subscribeToStream, true);

            // create builder

            DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
            builder.setToken(settings.getBotToken());
            builder.setAutoReconnect(true);

            //builder.setGame(settings.isDevBot() ? Game.listening("code") : setDefaultPlaying());
            builder.setGame(Game.listening("startup code."));
            // add event listeners
            builder.addEventListeners(new ReadyListener(this));
            builder.addEventListeners(new CommandListener(this));
            builder.addEventListeners(new MessageListener(this));
            builder.addEventListeners(new AutoModListener(this));
            builder.addEventListeners(new ModLogListener(this));

            // build & login
            this.shardManager = builder.build();

            defaultGameLoop();
        } catch (JedisConnectionException | LoginException | NumberFormatException | InterruptedException e) {
            e.printStackTrace();
            logger.wtf("BOOTING FAILED - SHUTTING DOWN");
            System.exit(-5);
        }
    }

    private void setGame(Game game) {
        shardManager.getShards().forEach(shard -> shard.getPresence().setGame(game));
    }

    private void defaultGameLoop() {
        if (settings.isDevBot()) {
            setGame(Game.listening("code"));
            return;
        }

        List<Game> gameStatuses = new ArrayList<>();

        gameStatuses.add(Game.listening(new Stats().getUsers() + " users"));
        gameStatuses.add(Game.playing("type !help"));
        gameStatuses.add(Game.listening(new Stats().getGuilds() + " guilds"));
        gameStatuses.add(Game.watching("devibot.net"));

        AtomicInteger index = new AtomicInteger(- 1);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            index.getAndIncrement();
            if (index.get() > gameStatuses.size()) index.set(0);

            setGame(gameStatuses.get(index.get()));

        }, 0, 2, TimeUnit.MINUTES);
    }

    public void reboot(int minutes, MessageChannel channel) {
        AtomicInteger min = new AtomicInteger(minutes + 1);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            int next = min.decrementAndGet();
            setGame(Game.playing("rebooting in " + next + " min" + (next == 1 ? "" : "s")));
            if (next == 0) {
                if (channel != null) {
                    channel.sendMessage(getAnimatedEmotes().FixParrot().getAsMention() + " cya later alligator").complete();
                    System.exit(312);
                } else {
                    System.exit(312);
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private void initDailyReboot() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR) + 1);
        today.set(Calendar.HOUR_OF_DAY, 4);
        today.set(Calendar.MINUTE, 30);
        today.set(Calendar.SECOND, 0);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                reboot(15, null);
            }
        }, today.getTime(), TimeUnit.MICROSECONDS.convert(1, TimeUnit.DAYS));
        this.rebootTime = new Date(today.getTimeInMillis() + 900000);
    }

    public void changeTwitchSubscriptionStatus(Collection<String> streamIDs, boolean subscribe) {
        Thread thread = new Thread(() -> {
            Set<String> copy = new HashSet<>(streamIDs);
            //need this to avoid interrupt exception
            Set<String> remove = new HashSet<>();

            int attempt = 0;
            while (!copy.isEmpty()) {
                if (attempt != 0) {
                    try {
                        //sleep 1 min because of twitch rate limits
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                attempt++;

                for (String id : copy) {
                    Request.StringResponse response = new RequestBuilder(okHttpClient)
                            .setURL("https://api.twitch.tv/helix/webhooks/hub")
                            .setRequestType(Request.RequestType.POST)
                            //headers
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Client-ID", settings.getTwitchClientID())
                            .addHeader("Authorization", "Bearer " + getSettings().getTwitchSecret())
                            //body
                            .appendBody("hub.mode", subscribe ? "subscribe" : "unsubscribe")
                            .appendBody("hub.callback", "https://www.devibot.net/api/twitch/callback")
                            .appendBody("hub.topic", "https://api.twitch.tv/helix/streams?user_id=" + id)
                            .appendBody("hub.lease_seconds", 864000).build().asStringSync();

                    if (response != null && response.getStatus() != 202) {
                        logger.error("Failed to subscribe to twitch stream: " + id + ", retrying in 60 seconds.");
                    } else {
                        getRedisSender().hset("streams#2", id, String.valueOf(System.currentTimeMillis()));
                        remove.add(id);
                    }
                }
                //avoiding interrupt exception
                for (String rm : remove) {
                    copy.remove(rm);
                }
                remove.clear();
            }
        });
        thread.setName("Devi Twitch Webhook Thread");
        thread.start();
    }

    private void loadStreams() {
        MongoCollection<Document> streams = getDatabaseManager().getDatabase().getCollection("streams");
        AtomicInteger total = new AtomicInteger();
        streams.find().forEach((Consumer<? super Document>) document -> {
            String stream = document.getString("stream");
            if (!this.streams.containsKey(stream)) {
                this.streams.put(stream, new ArrayList<>());
            }
            total.getAndIncrement();
            this.streams.get(stream).add(document.getString("guild"));
        });
        logger.log("Loaded twitch streams (Total: " + total.get() + ")");
    }

    public void loadTranslations() {
        // connect to translations database
        MongoDatabase securityDatabase = databaseManager.getClient().getDatabase("website");
        MongoCollection<Document> translations = securityDatabase.getCollection("translations");

        // register translations
        AtomicInteger total = new AtomicInteger();
        translations.find().forEach((Consumer<? super Document>) document -> {
            int id = Integer.parseInt(document.getString("_id"));
            for (Language language : Language.values()) {
                String translation = document.getString(language.getRegistry().toLowerCase());
                if(translation != null && !translation.equals("none")) {
                    total.getAndIncrement();
                    deviTranslations.get(language).put(id, translation);
                }
            }
        });
        logger.log("Loaded translations (Total: " + total.get() + ")");
    }

    public String getTranslation(Language language, int id, Object ... args) {
        String translation = getTranslation(language, id);
        for (int i = 0; i < args.length; i++) {
            translation = translation.replace("{" + i  + "}", String.valueOf(args[i]));
        }
        return translation;
    }

    public String getTranslation(Language language, int id) {
        String translation = deviTranslations.get(language).get(id);
        if (translation == null) {
            if (language == Language.ENGLISH) {
                sendMessageToDevelopers("Translation for id `" + id + "` not found. Please fix this immediately @everyone");
                return "Failed to lookup the the translation for id `" + id + "`. This issue has been reported to our developers and will be fixed as soon as they see it.";
            }
            return deviTranslations.get(Language.ENGLISH).get(id);
        }
        return translation;

    }
    public DeviGuild getDeviGuild(String id) {
        try {
            return deviGuildLoadingCache.get(id);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Stats getCurrentStats() {
        return new Stats();
    }

    private JedisPubSub getJedisPubSub() {
        return new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                logger.log("Received a message from the website: " + message);
                //<editor-fold desc="devi_update channel">
                if (channel.equals("devi_update")) {
                    JSONObject update = new JSONObject(message);

                    DeviGuild deviGuild = getDeviGuild(update.getString("guild_id"));
                    deviGuild.getSettings().setStringValue(GuildSettings.Settings.PREFIX, update.getString("change_prefix"));
                    deviGuild.getSettings().setStringValue(GuildSettings.Settings.LANGUAGE, update.getString("change_language"));
                    deviGuild.getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED, update.getString("change_mod_log_settings").equals("true"));
                    deviGuild.getSettings().setStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL, update.getString("change_mod_log_channel"));
                    deviGuild.saveSettings();

                    JSONObject confirmObject = new JSONObject().put("guild_id", update.getString("guild_id"));
                    redisSender.publish("devi_update_confirm", confirmObject.toString());
                }
                //</editor-fold>

                //<editor-fold desc="devi_twitch_event channel">
                if (channel.equals("devi_twitch_event")) {
                    JSONObject object = new JSONArray(message).getJSONObject(0);

                    if (streams.containsKey(object.getString("user_id"))) {
                        for (String guildID : streams.get(object.getString("user_id"))) {
                            if (shardManager == null) return;

                            DeviGuild deviGuild = getDeviGuild(guildID);

                            Guild guild = null;
                            for (JDA jda : shardManager.getShards()) {
                                for (Guild g : jda.getGuilds()) {
                                    if (g.getId().equals(guildID)) {
                                        guild = g;
                                        break;
                                    }
                                }
                            }
                            if (guild == null) return;

                            TextChannel textChannel = DiscordUtils.getTextChannel(deviGuild.getSettings().getStringValue(GuildSettings.Settings.TWITCH_CHANNEL), guild);
                            if (textChannel == null) return;

                            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

                            JSONObject user = new RequestBuilder(okHttpClient).setURL("https://api.twitch.tv/helix/users?id=" + object.getString("user_id"))
                                    .addHeader("Client-ID", getSettings().getTwitchClientID())
                                    .addHeader("Authorization", "Bearer " + getSettings().getTwitchSecret())
                                    .setRequestType(Request.RequestType.GET).build().asJSONSync().getBody();

                            JSONObject game = new RequestBuilder(okHttpClient).setURL("https://api.twitch.tv/helix/games?id=" + object.getString("game_id"))
                                    .addHeader("Client-ID", getSettings().getTwitchClientID())
                                    .addHeader("Authorization", "Bearer " + getSettings().getTwitchSecret())
                                    .setRequestType(Request.RequestType.GET).build().asJSONSync().getBody();

                            if (user.getJSONArray("data").length() == 0) return;
                            JSONObject userData = user.getJSONArray("data").getJSONObject(0);
                            String url = "https://www.twitch.tv/" + userData.getString("login");

                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setColor(new Color(100, 65, 164));
                            builder.setAuthor(userData.getString("display_name"), url, "https://www.twitch.tv/p/assets/uploads/glitch_474x356.png");
                            builder.setImage(object.getString("thumbnail_url").replace("{width}", "480").replace("{height}", "270"));
                            builder.setDescription(object.getString("title"));
                            builder.addField(getTranslation(language, 208), String.valueOf(object.getInt("viewer_count")), true);

                            if (game.getJSONArray("data").length() != 0)
                                builder.addField(getTranslation(language, 209), game.getJSONArray("data").getJSONObject(0).getString("name"), true);

                            builder.setThumbnail(userData.getString("profile_image_url"));

                            logger.log("Sending stream announcement for twitch streamer " +
                                    userData.getString("display_name") + " (" + object.getString("user_id") + ") to guild " + guild.getName() + " (" + guild.getId() + " )");
                            MessageUtils.sendMessageAsync(textChannel, new MessageBuilder()
                                    .setContent(getTranslation(language, 211, userData.getString("display_name"), url))
                                    .setEmbed(builder.build()).build());
                            getRedisSender().hset("streams#1", object.getString("user_id"), userData.toString());
                        }
                    }
                }
                //</editor-fold>
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                logger.log("Subscribed to redis channel " + channel);
            }

            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                logger.log("Unsubscribe from redis channel " + channel);
            }
        };
    }

    public class Stats {

        private long shards;
        private long guilds;
        private long users;
        private long channels;
        private long ping;

        public Stats() {
            this.shards = shardManager.getShards().size();
            this.guilds = 0;
            this.users = 0;
            this.channels = 0;
            this.ping = 0;

            for (JDA jda : shardManager.getShards()) {
                for (Guild guild : jda.getGuilds()) {
                    this.guilds++;
                    for (Member ignored : guild.getMembers()) this.users++;
                    for (TextChannel ignored : guild.getTextChannels()) this.channels++;
                    for (VoiceChannel ignored : guild.getVoiceChannels()) this.channels++;
                }
                for (PrivateChannel ignored : jda.getPrivateChannels()) this.channels++;
                this.ping += jda.getPing();
            }
            this.ping = this.ping / this.shards;
        }

        public long getShards() {
            return shards;
        }

        public long getChannels() {
            return channels;
        }

        public long getGuilds() {
            return guilds;
        }

        public long getPing() {
            return ping;
        }

        public long getUsers() {
            return users;
        }
    }

    public void sendMessageToDevelopers(Object o) {
        AtomicReference<Guild> guild = new AtomicReference<>(null);
        shardManager.getShards().forEach(jda -> {
            Guild g = jda.getGuildById("392264119102996480");
            if (g != null) guild.set(g);
        });
        if (guild.get() != null) {
            TextChannel channel = guild.get().getTextChannelById("458740773614125076");
            System.out.println(channel);
            if (channel != null) {
                MessageUtils.sendMessageAsync(channel, o);
            }
        }
    }

    public void sendFeedbackMessage(Object o) {
        AtomicReference<Guild> guild = new AtomicReference<>(null);
        shardManager.getShards().forEach(jda -> {
            Guild g = jda.getGuildById("392264119102996480");
            if (g != null) guild.set(g);
        });
        if (guild.get() != null) {
            TextChannel channel = guild.get().getTextChannelById("472755048833613824");
            if (channel != null) {
                MessageUtils.sendMessageAsync(channel, o);
            }
        }
    }

    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }

    private DeviGuild createDeviGuild(String id) {
        return new DeviGuild(id, this);
    }

    public Settings getSettings() {
        return settings;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public List<String> getAdmins() {
        return admins;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public int getSongsPlayed() {
        return songsPlayed;
    }

    public int getCommandsExecuted() {
        return commandsExecuted;
    }

    public void increaseSongsPlayed() {
        songsPlayed++;
    }

    public void increaseCommandsExecuted() {
        commandsExecuted++;
    }

    public HashMap<String, List<String>> getStreams() {
        return streams;
    }

    public Jedis getRedisSender() {
        return redisSender;
    }

    public ExpiringMap<String, String> getPrunedMessages() {
        return prunedMessages;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public Date getRebootTime() {
        return this.rebootTime;
    }

    public ResponseWaiter getResponseWaiter() {
        return responseWaiter;
    }

    public List<ModuleType> getDisabledModules() {
        return disabledModules;
    }

    public Logger getLogger() {
        return logger;
    }

    public HashMap<Language, HashMap<Integer, String>> getDeviTranslations() {
        return deviTranslations;
    }

    public List<String> getVoters() {
        return voters;
    }

    public AnimatedEmote getAnimatedEmotes() {
        return animatedEmotes;
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }
}