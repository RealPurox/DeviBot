package me.purox.devi.manager.punishments;

import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import net.dv8tion.jda.core.entities.Member;

import java.util.Date;

@SuppressWarnings("ALL")
public class Punishment {

    private Devi devi;
    private PunishmentManager punishmentManager;

    private DeviGuild deviGuild = null;
    private int caseNr;
    private Punishment.PunishmentType type;
    private Member punisher = null;
    private Member punished = null;
    private String reason = null;
    private Date date = null;
    private String messageId = null;
    private String channelId = null;

    public Punishment(Devi devi) {
        this.devi = devi;
        this.punishmentManager = punishmentManager;
        this.deviGuild = deviGuild;
        this.caseNr = caseNr;
        this.type = type;
        this.punisher = punisher;
        this.punished = punished;
        this.reason = reason;
        this.date = date;
        this.messageId = messageId;
        this.channelId = channelId;
    }

    public enum PunishmentType {
        BAN, SOFTBAN, MUTE, KICK, VOICEKICK
    }


    Punishment(String guildId, int caseNr, PunishmentType type, String punisherId, String punishedId, String reason, Date date, String messageId, String channelId) {

    }

    public Punishment execute() {
        return this;
    }

}
