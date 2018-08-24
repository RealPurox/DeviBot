package me.purox.devi.punishments;

import me.purox.devi.core.guild.DeviGuild;
import net.dv8tion.jda.core.entities.Member;

public class Punishment {

    public enum Type {
        BAN, SOFTBAN, MUTE, KICK, VOICEKICK
    }

    private DeviGuild deviGuild; //-
    private int caseId; //-
    private long time; //-
    private Punishment.Type type; //*
    private Member punisher; //*
    private Member punished; //*
    private String reason; //*
    private String messageId; //TODO
    private String channelId; //TODO

    Punishment(DeviGuild deviGuild, int caseId, long time, Type type, Member punisher, Member punished, String reason) {
        this.deviGuild = deviGuild;
        this.caseId = caseId;
        this.time = time;
        this.type = type;
        this.punisher = punisher;
        this.punished = punished;
        this.reason = reason;
    }

    public Punishment execute() {
        switch (type) {
            case BAN:
                break;
            case SOFTBAN:
                break;
            case MUTE:
                break;
            case KICK:
                break;
            case VOICEKICK:
                break;
        }
        return this;
    }

}
