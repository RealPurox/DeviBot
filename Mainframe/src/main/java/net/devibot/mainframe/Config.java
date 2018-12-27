package net.devibot.mainframe;

import java.io.*;

public class Config {

    private int port = 0;

    public int getPort() {
        return port;
    }

    public static Config loadConfig() {
        File file = new File("mainframe_config.json");
        Config config = new Config();

        if (!file.exists()) {
            try {
                FileWriter writer = new FileWriter(file);

                Mainframe.GSON.toJson(config, writer);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Config created ... shutting down.");
            System.exit(0);
        }

        try {
            FileReader reader = new FileReader(file);
            config = Mainframe.GSON.fromJson(reader, Config.class);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }
}
