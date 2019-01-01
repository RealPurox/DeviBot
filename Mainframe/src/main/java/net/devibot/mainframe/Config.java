package net.devibot.mainframe;

import net.devibot.core.Core;

import java.io.*;

public class Config {

    private int port = 0;
    private String databaseUrl = "";

    public int getPort() {
        return port;
    }

    public static Config loadConfig() {
        File file = new File("mainframe_config.json");
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
