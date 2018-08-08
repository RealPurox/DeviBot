package me.purox.devi.manager.punishments;

import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.manager.punishments.Punishment;
import net.dv8tion.jda.core.entities.Member;
import org.bson.Document;

import java.util.Date;

public class PunishmentManager {

    private Devi devi;

    public PunishmentManager(Devi devi) {
        this.devi = devi;
    }

    public void insertPunishment(DeviGuild deviGuild, int caseNr, Punishment.PunishmentType type, Member punished, Member punisher, String reason, Date date, String msgId, String channelId) {
        Document doc = new Document();
        doc.put("guildid", deviGuild.getId());
        doc.put("caseNr", caseNr);
        doc.put("type", type);
        doc.put("punished", punished.getUser().getId());
        doc.put("punisher", punisher.getUser().getId());
        doc.put("reason", reason);
        doc.put("date", date);
        doc.put("messageid", msgId);
        doc.put("channelid", channelId);
        devi.getDatabaseManager().saveToDatabase("punishments", doc);
    }
}
