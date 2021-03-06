package net.devibot.provider.core;

import net.devibot.core.request.RequestBuilder;
import net.devibot.provider.Config;
import net.devibot.provider.Provider;
import net.devibot.provider.commands.CommandHandler;
import net.devibot.provider.listener.CommandListener;
import net.devibot.provider.listener.GuildJoinLeaveListener;
import net.devibot.provider.listener.ReadyListener;
import net.devibot.provider.listener.automod.AutoModListener;
import net.devibot.provider.manager.AgentManager;
import net.devibot.provider.manager.CacheManager;
import net.devibot.provider.manager.MainframeManager;
import net.devibot.provider.utils.Translator;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class DiscordBot {

    private final Logger logger = LoggerFactory.getLogger(DiscordBot.class);

    private CommandHandler commandHandler;
    private AgentManager agentManager;

    private Provider provider;
    private ShardManager shardManager;

    private OkHttpClient okHttpClient = new OkHttpClient();

    // TODO: 17/02/2019 disable commands and such and don't execute mainframe requests to avoid exceptions 
    private boolean restrictedMode = false; //will be turned on when communication with mainframe is lost.

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
            builder.addEventListeners(new AutoModListener(this));

            this.shardManager = builder.build();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public RequestBuilder newRequestBuilder() {
        return new RequestBuilder(okHttpClient, getThreadPool());
    }

    public int getColor() {
        return getConfig().isDevMode() ? 0xF48924 : 0x7289DA;
    }

    public MainframeManager getMainframeManager() {
        return this.provider.getMainframeManager();
    }

    public ExecutorService getThreadPool() {
        return this.provider.getThreadPool();
    }

    public ScheduledExecutorService getScheduledThreadPool() {
        return this.provider.getScheduledThreadPool();
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

    public Provider getProvider() {
        return provider;
    }

    public void enableRestrictedMode() {
        logger.info("(X) Restriction mode has been enabled.");
        this.agentManager.getAgent(AgentManager.Type.MAINFRAME_INITIALIZER).start();
        this.restrictedMode = true;
    }

    public void disableRestrictedMode() {
        logger.info("(X) Restriction mode has been disabled.");
        this.agentManager.getAgent(AgentManager.Type.MAINFRAME_INITIALIZER).stop();
        this.restrictedMode = false;
    }

    public boolean isRestrictedMode() {
        return restrictedMode;
    }
}
