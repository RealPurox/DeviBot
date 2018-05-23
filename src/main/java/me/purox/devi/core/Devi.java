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

            // subscribe to twitch events
            for (String id : streams.keySet()) {
                subscribeToTwitchStream(id);
            }

            // create builder
            DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
            builder.setToken(settings.getBotToken());
            builder.setAutoReconnect(true);

            // make the dev bot listen to code | display website on main bot
            builder.setGame(settings.isDevBot() ? Game.listening("code") : Game.watching("devibot.net"));

            // add event listeners
            builder.addEventListeners(getCommandHandler().getCommands().get("mute"));
            builder.addEventListeners(new CommandListener(this));
            builder.addEventListeners(new ReadyListener(this));
            builder.addEventListeners(new MessageListener(this));
            builder.addEventListeners(new AutoModListener(this));
            builder.addEventListeners(new ModLogListener(this));

            // build & login
            this.shardManager = builder.build();
        } catch (JedisConnectionException | LoginException | UnirestException e) {
            e.printStackTrace();
            System.out.println("BOOTING FAILED - SHUTTING DOWN");
            System.out.println(e instanceof JedisConnectionException ? "(FAILED TO CONNECT TO REDIS SERVER)" : "");
            System.exit(0);
        }
    }

    public void unsubscribeFromTwitchStream(String id) throws UnirestException {
        String baseUrl = "https://api.twitch.tv/helix/webhooks/hub";

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Client-ID", settings.getTwitchClientID());

        JSONObject body = new JSONObject();
        body.put("hub.mode", "unsubscribe");
        body.put("hub.callback", "https://www.devibot.net/api/twitch/callback");
        body.put("hub.topic", "https://api.twitch.tv/helix/streams?user_id=" + id);

        HttpResponse<JsonNode> response = Unirest.post(baseUrl).headers(headers).body(body).asJson();
        if (response.getStatus() != 202) {
            System.out.println("[INFO] Failed to unsubscribe from twitch stream: " + id);
        }
    }

    public void subscribeToTwitchStream(String id) throws UnirestException {
        String baseUrl = "https://api.twitch.tv/helix/webhooks/hub";

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Client-ID", settings.getTwitchClientID());

        JSONObject body = new JSONObject();
        body.put("hub.mode", "subscribe");
        body.put("hub.callback", "https://www.devibot.net/api/twitch/callback");
        body.put("hub.topic", "https://api.twitch.tv/helix/streams?user_id=" + id);
        body.put("hub.lease_seconds", 864000);

        HttpResponse<JsonNode> response = Unirest.post(baseUrl).headers(headers).body(body).asJson();
        if (response.getStatus() != 202) {
            System.out.println("[INFO] Failed to subscribe to twitch stream: " + id);
        }
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
            return deviTranslations.get(Language.ENGLISH).get(id) + " (" + language.name().toLowerCase() + " translation not found)";
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
                    System.out.println(object);

                    if (streams.containsKey(object.getString("user_id"))) {
                        for (String guildID : streams.get(object.getString("user_id"))) {
                            DeviGuild deviGuild = getDeviGuild(guildID);

                            Guild guild = shardManager.getGuildById(guildID);
                            if (guild == null) return;
                            System.out.println("GUILD NOT NULL");

                            TextChannel textChannel = DiscordUtils.getTextChannel(deviGuild.getSettings().getStringValue(GuildSettings.Settings.TWITCH_CHANNEL), guild);
                            if (textChannel == null) return;
                            System.out.println("TEXTCHANNEL NOT NULL");

                            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
                            try {
                                JSONObject user = Unirest.get("https://api.twitch.tv/helix/users?login=" + object.getString("user_id"))
                                        .header("Client-ID", getSettings().getTwitchClientID()).asJson().getBody().getObject();

                                JSONObject game = Unirest.get("https://api.twitch.tv/helix/games?id=" + object.getString("game_id"))
                                        .header("Client-ID", getSettings().getTwitchClientID()).asJson().getBody().getObject();

                                EmbedBuilder builder = new EmbedBuilder();
                                builder.setColor(new Color(100, 65, 164));
                                builder.setAuthor("Twitch Stream", null, "https://www.twitch.tv/p/assets/uploads/glitch_474x356.png");
                                builder.setImage(object.getString("thumbnail_url").replace("{width}", "1920").replace("{height}", "1080"));
                                builder.addField(getTranslation(language, 87	), object.getString("title"), true);
                                builder.addField(getTranslation(language, 208), String.valueOf(object.getInt("viewer_count")), true);
                                builder.addField(getTranslation(language, 209), game.getJSONArray("data").getJSONObject(0).getString("name"), true);
                                builder.setThumbnail(user.getJSONArray("data").getJSONObject(0).getString("profile_image_url"));

                                MessageUtils.sendMessageAsync(textChannel, builder.build());
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

    public void resetStats() {
        commandsExecuted = 0;
        songsPlayed = 0;
    }
}