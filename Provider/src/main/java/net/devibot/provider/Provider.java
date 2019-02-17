package net.devibot.provider;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.devibot.core.Core;
import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.service.ProviderService;
import net.devibot.provider.manager.CacheManager;
import net.devibot.provider.manager.MainframeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
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

    private String string;

    private Config config;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(100);

    private MainframeManager mainframeManager;
    private CacheManager cacheManager;

    private DiscordBot discordBot;

    public Provider() {
        this.config = Config.loadConfig();
        connect();
    }

    private Server server;

    private void connect() {
        //create server
        try {
            logger.info("(X) Initializing Provider ... ");
            //define cacheManager
            cacheManager = new CacheManager(this);
            //start service
            this.server = ServerBuilder.forPort(this.config.getPort())
                    .addService(new ProviderService(this))
                    .executor(threadPool)
                    .build().start();
            Core.setServer(server);

            //define mainframe
            mainframeManager = new MainframeManager(this);
            mainframeManager.initialRequest(true);
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

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    public ScheduledExecutorService getScheduledThreadPool() {
        return scheduledThreadPool;
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

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }
}
