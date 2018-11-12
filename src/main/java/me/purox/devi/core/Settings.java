package me.purox.devi.core;

public class Settings {

    private boolean devBot = true;

    public String getDefaultPrefix() {
        return devBot ? "." : "!";
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

    public String getSteamApiKey() {
        return System.getenv("STEAM_KEY");
    }

    public String getOsuApiKey() {
        return System.getenv("OSU_KEY");
    }

    public String getDeviAPIAuthorization() {
        return System.getenv("DEVI_AUTH");
    }

    public String getDiscordBotsDotOrgToken() {
        return System.getenv("DISCORD_BOTS_ORG_TOKEN");
    }

    public String getDiscordBotListComToken() {
        return System.getenv("DISCORD_BOT_LIST_COM_TOKEN");
    }

    public boolean isDevBot() {
        return devBot;
    }

    void disableDevBot() {
        this.devBot = false;
    }
}
