package me.purox.devi.manager.punishments;

import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import net.dv8tion.jda.core.entities.Member;

import java.util.Date;

@SuppressWarnings("ALL")
public class Punishment {

    private Devi devi;
    private PunishmentManager punish;

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
        this.punish = new PunishmentManager(devi);
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
        System.out.println("Executing a new punishment.");

        switch (type) {

            case BAN:
                punish.insertPunishment(deviGuild, caseNr, PunishmentType.BAN, punisher, punished, reason, date, messageId, channelId);
                break;
            case SOFTBAN:
                punish.insertPunishment(deviGuild, caseNr, PunishmentType.SOFTBAN, punisher, punished, reason, date, messageId, channelId);
                break;
            case MUTE:
                punish.insertPunishment(deviGuild, caseNr, PunishmentType.MUTE, punisher, punished, reason, date, messageId, channelId);
                break;
            case KICK:
                punish.insertPunishment(deviGuild, caseNr, PunishmentType.KICK, punisher, punished, reason, date, messageId, channelId);
                break;
            case VOICEKICK:
                punish.insertPunishment(deviGuild, caseNr, PunishmentType.VOICEKICK, punisher, punished, reason, date, messageId, channelId);
                break;
        }
        return this;
    }

}
