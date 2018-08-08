package me.purox.devi.manager.punishments;

import me.purox.devi.core.guild.DeviGuild;
import net.dv8tion.jda.core.entities.Member;

import java.util.Date;

@SuppressWarnings("ALL")
public class PunishmentBuilder {

    private DeviGuild deviGuild = null;
    private int caseNr;
    private Punishment.PunishmentType type;
    private Member punisher = null;
    private Member punished = null;
    private String reason = null;
    private Date date = null;
    private String messageId = null;
    private String channelId = null;

    public PunishmentBuilder() {
    }

    public Member setMod(Member punisher) {
        this.punisher = punisher;
        return punisher;
    }

    public Member setPunished(Member punished) {
        this.punished = punished;
        return punished;
    }

    public String setReason(String reason){
        this.reason = reason;
        return reason;
    }

    public Punishment.PunishmentType setType(Punishment.PunishmentType type) {
        this.type = type;
        return type;
    }

    public Punishment build() {
        return new Punishment(deviGuild.getId(), caseNr, type, punisher.getUser().getId(), punished.getUser().getId(), reason, date, messageId, channelId);
    }

}
