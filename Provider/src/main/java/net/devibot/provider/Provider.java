package net.devibot.provider;

import net.devibot.core.Core;
import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.manager.CacheManager;
import net.devibot.provider.manager.MainframeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Provider {

    private static Logger logger = LoggerFactory.getLogger(Provider.class);

    private static Provider provider;

    public static Provider getInstance() {
        return provider;
    }

    public static void main(String[] args) {
        Core.setup();

        try {
            provider = new Provider();
        } catch (Exception e) {
            logger.error("", e);
            System.exit(0);
        }
    }

    private Config config;
    private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1000);

    private MainframeManager mainframeManager;
    private CacheManager cacheManager;

    private DiscordBot discordBot;

    public Provider() {
        this.config = Config.loadConfig();
        connect();
    }

    private void connect() {
        //create server
        try {
            logger.info("(X) Initializing Mainframe ... ");
            //define cacheManager
            cacheManager = new CacheManager(this);

            //define mainframe
            mainframeManager = new MainframeManager(this);
            mainframeManager.initialRequest();
        } catch (Exception e) {
            logger.error("", e);
            System.exit(0);
        }
    }

    public void initializeDiscordBot() {
        this.discordBot = new DiscordBot(this);
    }

    public Config getConfig() {
        return config;
    }

    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }

    public MainframeManager getMainframeManager() {
        return mainframeManager;
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }
}
