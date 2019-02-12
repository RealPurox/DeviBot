package net.devibot.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Config {

    private String mongoUrl = "";
    private String errorWebhook = "";
    private String controlRoomWebhook = "";
    private String monitoringRoomWebhook = "";

    private boolean devMode = true;

    public String getMongoUrl() {
        return mongoUrl;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public String getErrorWebhook() {
        return errorWebhook;
    }

    public String getControlRoomWebhook() {
        return controlRoomWebhook;
    }

    public String getMonitoringRoomWebhook() {
        return monitoringRoomWebhook;
    }

    static Config loadConfig() {
        File file = new File("core_config.json");
        Config config = new Config();

        if (!file.exists()) {
            try {
                FileWriter writer = new FileWriter(file);

                Core.GSON.toJson(config, writer);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Config created ... shutting down.");
            System.exit(0);
        }

        try {
            FileReader reader = new FileReader(file);
            config = Core.GSON.fromJson(reader, Config.class);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }
}
