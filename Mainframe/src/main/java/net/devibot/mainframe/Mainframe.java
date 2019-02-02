package net.devibot.mainframe;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.devibot.core.Core;
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

    public Mainframe() {
        this.config = Config.loadConfig();
        connect();
    }

    private Server server;

    private void connect() {
        //create server
        try {
            //start service
            server = ServerBuilder.forPort(this.config.getPort())
                    .addService(new MainframeService(this))
                    .executor(threadPool)
                    .build().start();
            //block a thread
            threadPool.submit(() -> {
                try {
                    server.awaitTermination();
                } catch (Exception e) {
                    logger.error("", e);
                }
            });

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

}
