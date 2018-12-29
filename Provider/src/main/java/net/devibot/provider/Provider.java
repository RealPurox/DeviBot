package net.devibot.provider;

import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.*;
import net.devibot.provider.manager.MainframeManager;
import net.devibot.provider.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Provider {

    private static Logger logger = LoggerFactory.getLogger(Provider.class);
    public static final Gson GSON = new GsonBuilder().create();

    private static Provider provider;

    public static Provider getInstance() {
        return provider;
    }

    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        try {
            provider = new Provider();
        } catch (Exception e) {
            logger.error("", e);
            System.exit(0);
        }
    }

    private Config config;
    private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1000);

    private Server server;

    private MainframeManager mainframeManager;

    private int id;

    public Provider() {
        this.config = Config.loadConfig();
        connect();
    }


    private void connect() {
        //create server
        try {
            //start service
            server = ServerBuilder.forPort(this.config.getPort())
                    .addService(new GrpcService(this))
                    .build().start();
            //block a thread
            threadPool.submit(() -> {
                try {
                    server.awaitTermination();
                } catch (Exception e) {
                    logger.error("", e);
                }
            });

            logger.info("Provider running on port " + config.getPort() + ".");

            //define mainframe
            mainframeManager = new MainframeManager(this);
            mainframeManager.initialRequest();
        } catch (Exception e) {
            logger.error("", e);
            System.exit(0);
        }
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
