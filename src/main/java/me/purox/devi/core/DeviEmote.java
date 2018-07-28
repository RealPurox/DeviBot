package me.purox.devi.core;

public enum DeviEmote {

    ERROR("<:error:455049961831268354>"),
    SUCCESS("<:success:455049950930403348>"),
    MUTE("<:devi_mute:435785844759068672>"),
    BAN("<:devi_ban:435786345445851136>"),
    TWITCH("<:twitch:448893462767599616>"),
    INFO("<:info:458663927568400397>"),
    MUSIC("<:music:462383963739258890>");

    private String get;

    DeviEmote(String get) {
        this.get = get;
    }

    public String get() {
        return get;
    }

    @Override
    public String toString() {
        return get;
    }
}
