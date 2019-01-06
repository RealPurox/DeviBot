package net.devibot.provider.core;

import net.devibot.core.request.RequestBuilder;
import net.devibot.provider.Config;
import net.devibot.provider.Provider;
import net.devibot.provider.commands.CommandHandler;
import net.devibot.provider.listener.CommandListener;
import net.devibot.provider.listener.GuildJoinLeaveListener;
import net.devibot.provider.listener.ReadyListener;
import net.devibot.provider.manager.AgentManager;
import net.devibot.provider.manager.CacheManager;
import net.devibot.provider.manager.MainframeManager;
import net.devibot.provider.utils.Translator;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.concurrent.ScheduledExecutorService;

public class DiscordBot {

    private final Logger logger = LoggerFactory.getLogger(DiscordBot.class);

    private CommandHandler commandHandler;
    private AgentManager agentManager;

    private Provider provider;
    private ShardManager shardManager;

    private OkHttpClient okHttpClient = new OkHttpClient();

    public DiscordBot(Provider provider) {
        this.provider = provider;

        //initialize manager, translator, etc before starting the bot
        this.commandHandler = new CommandHandler(this);
        this.agentManager = new AgentManager(this);
        Translator.initialize();

        initialize();
    }

    private void initialize() {
        //Create ShardManager and connect to Discord
        try {
            DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
            builder.setToken(getConfig().getBotToken());
            builder.setAutoReconnect(true);
            builder.setHttpClient(okHttpClient);

            //listener
            builder.addEventListeners(new ReadyListener(this));
            builder.addEventListeners(new GuildJoinLeaveListener(this));
            builder.addEventListeners(new CommandListener(this));

            this.shardManager = builder.build();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public RequestBuilder newRequestBuilder() {
        return new RequestBuilder(okHttpClient, getThreadPool());
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

    public ShardManager getShardManager() {
        return shardManager;
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public CacheManager getCacheManager() {
        return provider.getCacheManager();
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
}
