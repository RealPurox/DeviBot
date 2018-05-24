package me.purox.devi.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.purox.devi.listener.*;
import me.purox.devi.core.guild.ModLogManager;
import me.purox.devi.commands.handler.CommandHandler;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.database.DatabaseManager;
import me.purox.devi.music.MusicManager;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Devi {

    private Settings settings;
    private MusicManager musicManager;
    private DatabaseManager databaseManager;
    private CommandHandler commandHandler;
    private ModLogManager modLogManager;
    private ShardManager shardManager;

    private List<String> admins = new ArrayList<>();
    private LoadingCache<String, DeviGuild> deviGuildLoadingCache;
    private HashMap<Language, HashMap<Integer, String>> deviTranslations = new HashMap<>();
    private HashMap<String, List<String>> streams = new HashMap<>();

    private Jedis redisSender;

    private int songsPlayed;
    private int commandsExecuted;

    public Devi() {
        // init handlers / managers / settings / utils
        this.commandHandler = new CommandHandler(this);
        this.settings = new Settings();
        this.musicManager = new MusicManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.modLogManager = new ModLogManager(this);
        new MessageUtils(this);

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
        if (Arrays.asList(args).contains("--devi")) this.settings.setDevBot(false);
        // connect to database
        databaseManager.connect();
        // load translations
        loadTranslations();
        // load streams
        loadStreams();
        try {
            // subscribe to redis channel async because it's blocking the current thread
            Thread redisThread = new Thread(() -> {
                redisSender = new Jedis("54.38.182.128");
                redisSender.auth(settings.getDeviAPIAuthorizazion());

                Jedis receiverRedis = new Jedis("54.38.182.128");
                receiverRedis.auth(settings.getDeviAPIAuthorizazion());
                receiverRedis.subscribe(getJedisPubSub(), "devi_update", "devi_twitch_event");
            });
            redisThread.setName("Devi Redis Thread");
            redisThread.start();

            // start console command listener async because it's blocking the current thread
            Thread consoleCommandThread = new Thread(() -> commandHandler.startConsoleCommandListener());
            consoleCommandThread.setName("Devi Console Command Thread");
            consoleCommandThread.start();

            //block current for 1 second to make sure redis is connected
            Thread.sleep(1000);

            // subscribe to twitch events
            Set<String> subscribeToStream = new HashSet<>();
            for (String streamID : streams.keySet()) {
                //get time of last subscription
                String lastSubscription = getRedisSender().hget("streams#2", streamID);
                //there is not record of a last subscription, subscribe to stream events.
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
                    System.out.println("Subscription to " + streamID + " was more than 5 days ago, renewing ...");
                    subscribeToStream.add(streamID);
                }
            }
            //re-sub to them bebs
            changeTwitchSubscriptionStatus(subscribeToStream, true);

            // create builder
            DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
            builder.setToken(settings.getBotToken());
            builder.setAutoReconnect(true);

            // make the dev bot listen to code | display website on main bot
            builder.setGame(settings.isDevBot() ? Game.listening("code") : Game.watching("devibot.net"));

            // add event listeners
            builder.addEventListeners(new CommandListener(this));
            builder.addEventListeners(new ReadyListener(this));
            builder.addEventListeners(new MessageListener(this));
            builder.addEventListeners(new AutoModListener(this));
            builder.addEventListeners(new ModLogListener(this));
            builder.addEventListeners(getCommandHandler().getCommands().get("mute"));

            // build & login
            this.shardManager = builder.build();
        } catch (JedisConnectionException | LoginException | NumberFormatException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("BOOTING FAILED - SHUTTING DOWN");
            System.out.println(e instanceof JedisConnectionException ? "(FAILED TO CONNECT TO REDIS SERVER)" : "");
            System.exit(0);
        }
    }

    public void changeTwitchSubscriptionStatus(Collection<String> streamIDs, boolean subscribe) {
        Thread thread = new Thread(() -> {
            Set<String> copy = new HashSet<>(streamIDs);
            Set<String> remove = new HashSet<>();

            String baseUrl = "https://api.twitch.tv/helix/webhooks/hub";

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Client-ID", settings.getTwitchClientID());
            headers.put("Authorization", "Bearer " + getSettings().getTwitchSecret());


            int attempt = 0;
            while (!copy.isEmpty()) {
                if (attempt != 0) {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                attempt++;

                for (String id : copy) {
                    JSONObject body = new JSONObject();
                    body.put("hub.mode", subscribe ? "subscribe" : "unsubscribe");
                    body.put("hub.callback", "https://www.devibot.net/api/twitch/callback");
                    body.put("hub.topic", "https://api.twitch.tv/helix/streams?user_id=" + id);
                    if (subscribe) body.put("hub.lease_seconds", 864000);


                    HttpResponse<JsonNode> response;
                    try {
                        response = Unirest.post(baseUrl).headers(headers).body(body).asJson();
                    } catch (UnirestException e) {
                        response = null;
                    }

                    if (response == null) {
                        System.err.println("[ERROR] Response for stream " + id + " is null");
                    }

                    if (response != null && response.getStatus() != 202) {
                        System.err.println("[INFO] Failed to subscribe to twitch stream: " + id + ", retrying in 60 seconds.");
                    } else {
                        getRedisSender().hset("streams#2", id, String.valueOf(System.currentTimeMillis()));
                        remove.add(id);
                    }
                }
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
        streams.find().forEach((Consumer<? super Document>) document -> {
            String stream = document.getString("stream");
            if (!this.streams.containsKey(stream)) {
                this.streams.put(stream, new ArrayList<>());
            }
            this.streams.get(stream).add(document.getString("guild"));
        });
    }

    public void loadTranslations() {
        // connect to translations database
        MongoDatabase securityDatabase = databaseManager.getClient().getDatabase("website");
        MongoCollection<Document> translations = securityDatabase.getCollection("translations");

        // register translations
        translations.find().forEach((Consumer<? super Document>) document -> {
            int id = Integer.parseInt(document.getString("_id"));
            for (Language language : Language.values()) {
                String translation = document.getString(language.getRegistry().toLowerCase());
                if(translation != null && !translation.equals("none")) {
                    deviTranslations.get(language).put(id, translation);
                }
            }
        });
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

    private JedisPubSub getJedisPubSub() {
        return new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
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
                if (channel.equals("devi_twitch_event")) {
                    JSONObject object = new JSONArray(message).getJSONObject(0);

                    if (streams.containsKey(object.getString("user_id"))) {
                        for (String guildID : streams.get(object.getString("user_id"))) {
                            DeviGuild deviGuild = getDeviGuild(guildID);

                            Guild guild = shardManager.getGuildById(guildID);
                            if (guild == null) return;

                            TextChannel textChannel = DiscordUtils.getTextChannel(deviGuild.getSettings().getStringValue(GuildSettings.Settings.TWITCH_CHANNEL), guild);
                            if (textChannel == null) return;

                            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
                            try {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Client-ID", getSettings().getTwitchClientID());
                                headers.put("Authorization", "Bearer " + getSettings().getTwitchSecret());

                                JSONObject user = Unirest.get("https://api.twitch.tv/helix/users?id=" + object.getString("user_id"))
                                        .headers(headers).asJson().getBody().getObject();

                                JSONObject game = Unirest.get("https://api.twitch.tv/helix/games?id=" + object.getString("game_id"))
                                        .headers(headers).asJson().getBody().getObject();

                                if (user.getJSONArray("data").length() == 0) return;
                                JSONObject userData = user.getJSONArray("data").getJSONObject(0);
                                String url = "https://www.twitch.tv/" + userData.getString("login");

                                EmbedBuilder builder = new EmbedBuilder();
                                builder.setColor(new Color(100, 65, 164));
                                builder.setAuthor(userData.getString("display_name"), url, "https://www.twitch.tv/p/assets/uploads/glitch_474x356.png");
                                builder.setImage(object.getString("thumbnail_url").replace("{width}", "1920").replace("{height}", "1080"));
                                builder.setDescription(object.getString("title"));
                                builder.addField(getTranslation(language, 208), String.valueOf(object.getInt("viewer_count")), true);

                                if (game.getJSONArray("data").length() != 0)
                                    builder.addField(getTranslation(language, 209), game.getJSONArray("data").getJSONObject(0).getString("name"), true);

                                builder.setThumbnail(userData.getString("profile_image_url"));

                                MessageUtils.sendMessageAsync(textChannel, new MessageBuilder()
                                        .setContent(getTranslation(language, 211, userData.getString("display_name"), url))
                                        .setEmbed(builder.build()).build());
                                getRedisSender().hset("streams#1", object.getString("user_id"), user.toString());
                            } catch (UnirestException e) {
                                return;
                            }
                        }
                    }
                }
            }

            @Override
            public void onSubscribe(String channel, int subscribedChannels) {
                System.out.println("Subscribed to redis channel " + channel);
            }

            @Override
            public void onUnsubscribe(String channel, int subscribedChannels) {
                System.out.println("Unsubscribe from redis channel " + channel);
            }
        };
    }

    public void startStatsPusher(){
        if (this.settings.isDevBot()) return;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("[HH:mm:ss.SSS]");
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                long shards = shardManager.getShards().size();
                long guilds = 0;
                long users = 0;
                long channels = 0;
                long ping = 0;

                for (JDA jda : shardManager.getShards()) {
                    for (Guild guild : jda.getGuilds()) {
                        guilds++;
                        for (Member ignored : guild.getMembers()) users++;
                        for (TextChannel ignored : guild.getTextChannels()) channels++;
                        for (VoiceChannel ignored : guild.getVoiceChannels()) channels++;
                    }
                    for (PrivateChannel ignored : jda.getPrivateChannels()) channels++;
                    ping += jda.getPing();
                }
                ping = ping / shards;

                //website
                JSONObject websiteObject = new JSONObject();

                websiteObject.put("shards", shards);
                websiteObject.put("guilds", guilds);
                websiteObject.put("users", users);
                websiteObject.put("channels", channels);
                websiteObject.put("average_ping", ping);

                HashMap<String, String> deviHeaders = new HashMap<>();
                deviHeaders.put("Authorization", "Bearer " + settings.getDeviAPIAuthorizazion());
                deviHeaders.put("Content-Type", "application/json");

                int websiteStatus = Unirest.post("https://www.devibot.net/api/stats")
                        .headers(deviHeaders)
                        .body(websiteObject)
                        .asJson().getStatus();


                //discordbots.org
                HashMap<String, String> discordBotsOrgHeaders = new HashMap<>();
                discordBotsOrgHeaders.put("Authorization", settings.getDiscordBotsDotOrgToken());

                int discordBotsStatus = Unirest.post("https://discordbots.org/api/bots/354361427731152907/stats")
                        .headers(discordBotsOrgHeaders)
                        .field("server_count", guilds)
                        .asJson().getStatus();

                if (!(websiteStatus == 200 || websiteStatus == 301) && !(discordBotsStatus == 200 | discordBotsStatus == 301))
                    System.out.println(simpleDateFormat.format(new Date())+ " Failed to push stats (invalid status)");
            } catch (UnirestException e) {
                System.out.println(simpleDateFormat.format(new Date())+ " Failed to push stats (" + e.getMessage() + ")");
            }

        }, 2, 2, TimeUnit.MINUTES);
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

    public ModLogManager getModLogManager() {
        return modLogManager;
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
}