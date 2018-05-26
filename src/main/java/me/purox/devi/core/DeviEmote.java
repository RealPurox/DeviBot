package me.purox.devi.core;

public enum DeviEmote {

    MUTE("<:devi_mute:435785844759068672>"),
    BAN("<:devi_ban:435786345445851136>"),
    BAN_HAMMER("<:banhammer:440940769751334922>"),
    DISCORD_LOGO("<:discord_logo:435796365243842580>"),
    ADVERTISEMENT("<:advertisement:440942456360402944>"),
    TWITCH("<:twitch:448893462767599616>");

    private String get;
    DeviEmote(String get) {
        this.get = get;
    }

    public String get() {
        return get;
    }
}
