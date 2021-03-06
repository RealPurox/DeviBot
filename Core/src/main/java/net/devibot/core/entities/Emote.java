package net.devibot.core.entities;

public enum Emote {

    // error/success
    ERROR("<:error:455049961831268354>"),
    SUCCESS("<:success:455049950930403348>"),
    // misc stuff,
    INFO("<:info:458663927568400397>"),
    BAN("<:ban:531406340187488259>"),
    TWITCH("<:twitch:531406978409431052>"),

    AUTO_MOD("<:auto_mod:533766584003723311>"),
    ADVERTISING("<:advertising:533771147091771402>"),
    MENTION("<:mention:534038181063163934>"),
    INVITE("<:invite:534035985470717953>"),
    SPAM("<:spam:534036445409574912>"),
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
