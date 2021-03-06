package net.devibot.mainframe;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.devibot.core.Core;
import net.devibot.mainframe.manager.AgentManager;
import net.devibot.mainframe.manager.ProviderManager;
import net.devibot.mainframe.service.MainframeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Mainframe {

    private static Logger logger = LoggerFactory.getLogger(Mainframe.class);

    private static Mainframe mainframe;

    public static void main(String[] args) {
        Core.setup();

        try {
            mainframe = new Mainframe();
        } catch (Exception e) {
            logger.error("", e);
            System.exit(0);
        }
    }

    private Config config;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(100);

    private ProviderManager providerManager;
    private AgentManager agentManager;

    public Mainframe() {
        this.config = Config.loadConfig();
        this.providerManager = new ProviderManager(this);
        this.agentManager = new AgentManager(this);
        connect();
    }

    private Server server;

    private void connect() {
        //create server
        try {
            logger.info("(X) Initializing Mainframe ... ");
            //start service
            server = ServerBuilder.forPort(this.config.getPort())
                    .addService(new MainframeService(this))
                    .executor(threadPool)
                    .build().start();
            Core.setServer(server);
            //block a thread
            threadPool.submit(() -> {
                try {
                    server.awaitTermination();
                } catch (Exception e) {
                    logger.error("", e);
                }
            });

            agentManager.startAllAgents();
            logger.info("Mainframe running on port " + config.getPort() + ".");
        } catch (Exception e) {
            logger.error("", e);
            System.exit(0);
        }
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

    public ProviderManager getProviderManager() {
        return providerManager;
    }
}
