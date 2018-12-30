package net.devibot.mainframe;

import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import net.devibot.mainframe.service.GeneralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Mainframe {

    private static Logger logger = LoggerFactory.getLogger(Mainframe.class);
    public static final Gson GSON = new GsonBuilder().create();

    private static Mainframe mainframe;

    public static void main(String[] args) {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        try {
            mainframe = new Mainframe();
        } catch (Exception e) {
            logger.error("", e);
            System.exit(0);
        }
    }

    private Config config;
    private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1000);

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
                    .addService(new GeneralService(this))
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

    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }
}
