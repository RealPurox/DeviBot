package me.purox.devi.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import me.purox.devi.commands.guild.automod.AutoModListener;
import me.purox.devi.commands.guild.modlog.ModLogManager;
import me.purox.devi.commands.handler.CommandHandler;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.database.DatabaseManager;
import me.purox.devi.listener.CommandListener;
import me.purox.devi.listener.MessageListener;
import me.purox.devi.listener.ReadyListener;
import me.purox.devi.music.MusicManager;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Game;
import org.bson.Document;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.security.auth.login.LoginException;
import java.util.*;
import java.util.concurrent.ExecutionException;
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
    private HashMap<Language, HashMap<Integer, String>> deviTranslations = new HashMap<>();
    private LoadingCache<String, DeviGuild> deviGuildLoadingCache;
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
        // connect to database and load translations
        databaseManager.connect();
        loadTranslations();

        // subscribe to redis channel
        Thread redisThread = new Thread(() -> {
            redisSender = new Jedis("54.38.182.128");
            redisSender.auth(settings.getDeviAPIAuthorizazion());

            Jedis receiverRedis = new Jedis("54.38.182.128");
            receiverRedis.auth(settings.getDeviAPIAuthorizazion());
            receiverRedis.subscribe(getJedisPubSub(), "devi_update");
        });
        redisThread.setName("Devi Redis Thread");
        redisThread.start();

        try {
            //create builder
            DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
            builder.setToken(settings.getBotToken());
            builder.setAutoReconnect(true);

            // make the dev bot listen to code | display website on main bot
            builder.setGame(settings.isDevBot() ? Game.listening("code") : Game.watching("devibot.net"));

            //add event listeners
            builder.addEventListeners(new CommandListener(this));
            builder.addEventListeners(new ReadyListener(this));
            builder.addEventListeners(new MessageListener(this));
            builder.addEventListeners(getCommandHandler().getCommands().get("mute"));
            builder.addEventListeners(new AutoModListener(this));

            // build & login
            this.shardManager = builder.build();
        } catch (JedisConnectionException | LoginException e) {
            System.out.println("BOOTING FAILED - SHUTTING DOWN");
            System.out.println(e instanceof JedisConnectionException ? "(FAILED TO CONNECT TO REDIS SERVER)" : "");
            System.exit(0);
        }
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


    public void startStatsPusher(){
        TimerTask tenSecTask = new TimerTask() {
            @Override
            public void run() {
                pushStats();
            }
        };

        Timer timer = new Timer();
        timer.schedule(tenSecTask, 0, 10000);
    }

    private void pushStats() {
        if (settings.isDevBot()) return;
        try {
            JSONObject object = new JSONObject();

            // 0 = shards; 1 = guilds; 2 = users; 3 = channels;
            long[] data = new long[5];

            data[0] = shardManager.getShards().size();
            object.put("shards", data[0]);

            shardManager.getShards().forEach(jda -> data[1] += jda.getGuilds().size());
            object.put("guilds", data[1]);

            shardManager.getShards().forEach(jda -> jda.getGuilds().forEach(guild -> data[2] += guild.getMembers().size()));
            object.put("users", data[2]);

            shardManager.getShards().forEach(jda -> data[3] += (jda.getPrivateChannels().size() + jda.getTextChannels().size() + jda.getVoiceChannels().size()));
            object.put("channels", data[3]);

            data[4] = (long) shardManager.getAveragePing();
            object.put("average_ping", shardManager.getAveragePing());

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + settings.getDeviAPIAuthorizazion());
            headers.put("Content-Type", "application/json");

            String response = Unirest.post("https://www.devibot.net/api/stats").headers(headers).body(object).asString().getBody();
        } catch (UnirestException e) {
            //e.printStackTrace();
            //^^^^ this shit just spams the console when the website is offline
        }
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
