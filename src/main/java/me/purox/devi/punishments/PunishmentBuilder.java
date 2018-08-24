package me.purox.devi.punishments;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import me.purox.devi.core.guild.DeviGuild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.utils.Checks;

public class PunishmentBuilder {

    private DeviGuild deviGuild; //auto
    private int caseId; //auto
    private long time; //auto
    private Punishment.Type type; //required
    private Member punisher; //required
    private Member punished; //required
    private String reason; //required

    public PunishmentBuilder(DeviGuild deviGuild) {
        this.deviGuild = deviGuild;
        this.caseId = deviGuild.getDevi().getDatabaseManager().getDatabase().getCollection("punishments")
                .find(Filters.eq("guild_id", deviGuild.getId()))
                .filter(new BasicDBObject("caseId", -1))
                .limit(1).first().getInteger("caseId") + 1;
        this.time = System.currentTimeMillis();
    }

    //Required
    public PunishmentBuilder setType(Punishment.Type type) {
        this.type = type;
        return this;
    }

    public PunishmentBuilder setPunisher(Member punisher) {
        this.punisher = punisher;
        return this;
    }

    public PunishmentBuilder setPunished(Member punished) {
        this.punished = punished;
        return this;
    }

    public PunishmentBuilder setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public Punishment build() {
        Checks.notNull(type, "type");
        Checks.notNull(punisher, "punisher");
        Checks.notNull(punished, "punished");
        Checks.notNull(reason, "reason");
        return new Punishment(deviGuild, caseId, time, type, punisher, punished, reason);
    }

}
