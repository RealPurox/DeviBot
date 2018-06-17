package me.purox.devi.core;

public class Settings {

    private boolean devBot = true;

    public String getDefaultPrefix() {
        return devBot ? "<" : "!";
    }

    String getBotToken() {
        if (devBot) return System.getenv("DEVI_DEV_TOKEN");
        return System.getenv("DEVI_TOKEN");
    }

    public String getTwitchClientID(){
        return System.getenv("DEVI_TWITCH");
    }

    public String getTwitchSecret() {
        return System.getenv("DEVI_TWITCH_SECRET");
    }

    public String getMongoToken() {
        return System.getenv("MONGO_URL");
    }

    public String getHypixelAPIKey() {
        return System.getenv("HYPIXEL");
    }

    public String getFortniteApiKey() {
        return System.getenv("FTN");
    }

    String getDeviAPIAuthorization() {
        return "123";
    }

    String getDiscordBotsDotOrgToken() {
        return System.getenv("DISCORD_BOTS_ORG_TOKEN");
    }

    public boolean isDevBot() {
        return devBot;
    }

    void disableDevBot() {
        this.devBot = false;
    }
}
