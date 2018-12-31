package net.devibot.provider;

import java.io.*;

public class Config {

    private String mainframeIp = "";
    private int mainframePort = 0;

    private String botToken = "";
    private boolean devMode = true;

    private String websiteAuthenticationKey = "";

    private String discordBotsDotOrgToken = "";

    public String getMainframeIp() {
        return mainframeIp;
    }

    public int getMainframePort() {
        return mainframePort;
    }

    public String getBotToken() {
        return botToken;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public String getWebsiteAuthenticationKey() {
        return websiteAuthenticationKey;
    }

    public String getDiscordBotsDotOrgToken() {
        return discordBotsDotOrgToken;
    }

    static Config loadConfig() {
        File file = new File("provider_config.json");
        Config config = new Config();

        if (!file.exists()) {
            try {
                FileWriter writer = new FileWriter(file);

                Provider.GSON.toJson(config, writer);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Config created ... shutting down.");
            System.exit(0);
        }

        try {
            FileReader reader = new FileReader(file);
            config = Provider.GSON.fromJson(reader, Config.class);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }
}
