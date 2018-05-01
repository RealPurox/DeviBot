package me.purox.devi.core;

public class Settings {

    private boolean devBot = true;

    public String getDefaultPrefix() {
        return "!";
    }

    String getBotToken() {
        if (devBot) return System.getenv("DEVI_DEV_TOKEN");
        return System.getenv("DEVI_TOKEN");
    }

    public String getTwitchClientID(){
        return System.getenv("DEVI_TWITCH");
    }

    public String getMongoToken() {
        return System.getenv("MONGO_URL");
    }

    String getDeviAPIAuthorizazion() {
        return System.getenv("DEVI_AUTH");
    }

    public boolean isDevBot() {
        return devBot;
    }
}
