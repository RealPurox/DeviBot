package me.purox.devi.punishments;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.sun.istack.internal.NotNull;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.punishments.options.*;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.utils.Checks;
import org.bson.Document;

public class PunishmentBuilder {

    private DeviGuild deviGuild; //auto
    private int caseId; //auto
    private long time; //auto
    private Punishment.Type type; //required
    private Member punisher; //required
    private Member punished; //required
    private String reason; //required
    private Options options; //optional

    public PunishmentBuilder(DeviGuild deviGuild) {
        this.deviGuild = deviGuild;
        Document first = deviGuild.getDevi().getDatabaseManager().getDatabase().getCollection("punishments")
                .find(Filters.eq("guild_id", deviGuild.getId()))
                .filter(new BasicDBObject("caseId", -1))
                .limit(1).first();
        this.caseId = first == null ? 1 : first.getInteger("caseId");
        this.time = System.currentTimeMillis();
    }

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

    public PunishmentBuilder setOptions(Options options) {
        this.options = options;
        return this;
    }

    public Punishment build() {
        JavaUtils.notNull(type, "type");
        JavaUtils.notNull(punisher, "punisher");
        JavaUtils.notNull(punished, "punished");
        JavaUtils.notNull(reason, "reason");

        if (this.options != null) {
            if (this.type == Punishment.Type.BAN && !(this.options instanceof BanOptions))
                throw new IllegalArgumentException(options.getClass().getName() + " cannot be used for Punishment.Type " + this.type);
            if (this.type == Punishment.Type.SOFTBAN && !(this.options instanceof SoftbanOptions))
                throw new IllegalArgumentException(options.getClass().getName() + " cannot be used for Punishment.Type " + this.type);
            if (this.type == Punishment.Type.MUTE && !(this.options instanceof MuteOptions))
                throw new IllegalArgumentException(options.getClass().getName() + " cannot be used for Punishment.Type " + this.type);
            if (this.type == Punishment.Type.KICK && !(this.options instanceof KickOptions))
                throw new IllegalArgumentException(options.getClass().getName() + " cannot be used for Punishment.Type " + this.type);
            if (this.type == Punishment.Type.VOICEKICK && !(this.options instanceof VoicekickOptions))
                throw new IllegalArgumentException(options.getClass().getName() + " cannot be used for Punishment.Type " + this.type);
        }

        return new Punishment(deviGuild, caseId, time, type, punisher, punished, reason, options);
    }

}
