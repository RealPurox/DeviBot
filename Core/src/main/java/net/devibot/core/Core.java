package net.devibot.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.Server;
import net.devibot.core.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Core {

    private static final Logger logger = LoggerFactory.getLogger(Core.class);

    public enum Type {
        MAINFRAME, PROVIDER,
    }

    //CONFIG
    public static Config CONFIG;
    //GSON
    public static final Gson GSON = new GsonBuilder().create();
    public static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();
    //Core Type
    public static Type TYPE;

    //Server
    private static Server server;

    public static void setup() {
        //load config
        CONFIG = Config.loadConfig();

        //define type
        try {
            Class.forName("net.devibot.provider.Provider");
            TYPE = Type.PROVIDER;
        } catch (ClassNotFoundException e) {
            TYPE = Type.MAINFRAME;
        }

        //add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down ..");
            DatabaseManager.getInstance().pushLogs();

            if (server != null) {
                server.shutdown();
            }
        }));
    }

    public static void setServer(Server server) {
        Core.server = server;
    }

    public static Server getServer() {
        return Core.server;
    }
}
