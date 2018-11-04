package me.purox.devi.entities;

public enum Emote {

    //error/success
    ERROR("<:error:455049961831268354>"),
    SUCCESS("<:success:455049950930403348>"),
    //punishments
    MUTE("<:devi_mute:435785844759068672>"),
    BAN("<:devi_ban:435786345445851136>"),
    //misc
    TWITCH("<:twitch:448893462767599616>"),
    INFO("<:info:458663927568400397>"),
    MUSIC("<:music:462383963739258890>"),
    // statuses
    ONLINE("<:online:473958665725149194>"),
    AWAY("<:away:473958590844108801>"),
    OFFLINE("<:offline:473958621080846346>"),
    DO_NOT_DISTURB("<:dnd:473958535018053643>");

    private String get;

    Emote(String get) {
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

