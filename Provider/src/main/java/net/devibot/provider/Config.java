package net.devibot.provider;

import net.devibot.core.Core;

import java.io.*;

public class Config {

    private String mainframeIp = "";
    private int mainframePort = 0;

    private String botToken = "";
    private boolean devMode = true;

    private String defaultPrefix = "";

    private String websiteAuthenticationKey = "";

    private String discordBotsDotOrgToken = "";

    private String[] developers = new String[0];

    private String controlRoomWebhook = "";

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

    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    public String[] getDevelopers() {
        return developers;
    }

    public String getControlRoomWebhook() {
        return controlRoomWebhook;
    }

    static Config loadConfig() {
        File file = new File("provider_config.json");
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
