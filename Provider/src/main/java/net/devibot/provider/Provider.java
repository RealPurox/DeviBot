package net.devibot.provider;

import ch.qos.logback.classic.Level;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.ServerBuilder;
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
            if (args.length < 2 || !args[0].equalsIgnoreCase("-port"))
                throw new Exception("Please provide a port");
            provider = new Provider(Integer.parseInt(args[1]));
        } catch (Exception e) {
            logger.error("", e);
            System.exit(0);
        }
    }

    private Config config;
    private ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(100);

    private ManagedChannel mainframe;

    public Provider(int port) {
        this.config = Config.loadConfig();
        connect();
    }

    private Server server;

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

            logger.info("Running on port " + config.getPort() + ". Now attempting to connect to Mainframe.");

        } catch (Exception e) {
            System.exit(0);
        }
    }

    public Config getConfig() {
        return config;
    }
}
