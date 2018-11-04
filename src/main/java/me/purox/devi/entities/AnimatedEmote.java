package me.purox.devi.entities;

import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;

public class AnimatedEmote {

    private Devi devi;
    private String guildId;

    public AnimatedEmote(Devi devi) {
        this.devi = devi;
        this.guildId = "392264119102996480";
    }

    public Emote PartyParrotEmote() {
        Guild g = devi.getShardManager().getGuildById(guildId);
        if (g == null) return null;
        Emote e = g.getEmotesByName("partyparrot", true).get(0);
        if (e == null) return null;
        return e;
    }

    public Emote CristmasParrotEmote() {
        Guild g = devi.getShardManager().getGuildById(guildId);
        if (g == null) return null;
        Emote e = g.getEmotesByName("christmasparrot", true).get(0);
        if (e == null) return null;
        return e;
    }

    public Emote UpvoteParrot() {
        Guild g = devi.getShardManager().getGuildById(guildId);
        if (g == null) return null;
        Emote e = g.getEmotesByName("upvotepartyparrot", true).get(0);
        if (e == null) return null;
        return e;
    }

    public Emote FixParrot() {
        Guild g = devi.getShardManager().getGuildById(guildId);
        if (g == null) return null;
        Emote e = g.getEmotesByName("fixparrot", true).get(0);
        if (e == null) return null;
        return e;
    }

    public Emote HalloweenParrot() {
        Guild g = devi.getShardManager().getGuildById(guildId);
        if (g == null) return null;
        Emote e = g.getEmotesByName("halloweenparrot", true).get(0);
        if (e == null) return null;
        return e;
    }

    public Emote EvilParrot() {
        Guild g = devi.getShardManager().getGuildById(guildId);
        if (g == null) return null;
        Emote e = g.getEmotesByName("evilparrot", true).get(0);
        if (e == null) return null;
        return e;
    }
}