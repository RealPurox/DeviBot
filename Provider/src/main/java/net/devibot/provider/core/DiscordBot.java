package net.devibot.provider.core;

import net.devibot.provider.Config;
import net.devibot.provider.Provider;
import net.devibot.provider.manager.MainframeManager;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

public class DiscordBot {

    private final Logger logger = LoggerFactory.getLogger(DiscordBot.class);

    private Provider provider;
    private ShardManager shardManager;

    private OkHttpClient okHttpClient = new OkHttpClient();

    public DiscordBot(Provider provider) {
        this.provider = provider;
        initialize();
    }

    private void initialize() {
        //Create ShardManager and connect to Discord
        try {
            DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
            builder.setToken(getConfig().getBotToken());
            builder.setAutoReconnect(true);
            builder.setHttpClient(okHttpClient);

            this.shardManager = builder.build();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public Color getColor() {
        return getConfig().isDevMode() ? Color.decode("#F48924") : Color.decode("#7289DA");
    }

    public MainframeManager getMainframeManager() {
        return this.provider.getMainframeManager();
    }

    public ScheduledExecutorService getThreadPool() {
        return this.provider.getThreadPool();
    }

    public Config getConfig() {
        return this.provider.getConfig();
    }
}
