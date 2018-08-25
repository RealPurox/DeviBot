package me.purox.devi.punishments;

import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.punishments.options.BanOptions;
import me.purox.devi.punishments.options.Options;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.function.Consumer;

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
    private Options options;

    Punishment(DeviGuild deviGuild, int caseId, long time, Type type, Member punisher, Member punished, String reason, Options options) {
        this.deviGuild = deviGuild;
        this.caseId = caseId;
        this.time = time;
        this.type = type;
        this.punisher = punisher;
        this.punished = punished;
        this.reason = reason;
        this.options = options;
    }

    @Override
    public String toString() {
        return "Guild: " + deviGuild.getId() + "\n" +
                "caseId: " + caseId + "\n" +
                "time: " + time + "\n" +
                "type: " + type + "\n" +
                "punisher: " + punisher + "\n" +
                "punished: " + punished + "\n" +
                "reason: " + reason + "\n" +
                "messageId: " + messageId + "\n" +
                "channelId: " + channelId + "\n";
    }

    public void execute(Consumer<? super Void> success, Consumer<? super Throwable> error) {
        System.out.println("Executing Punishment Type : " + type);
        System.out.println("Options: " + options);

        Guild guild = deviGuild.getDevi().getShardManager().getGuildById(deviGuild.getId());
        if (guild == null) {
            System.out.println("Guild not cached");
            return;
        }

        TextChannel logChannel = guild.getTextChannelById(deviGuild.getSettings().getStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL));
        if (logChannel == null) {
            System.out.println("Log channel not found (" + deviGuild.getSettings().getStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL) + ")");
            return;
        }

        this.channelId = logChannel.getId();

        MessageUtils.sendMessageAsync(logChannel, toString(), msg -> {
            this.messageId = msg.getId();

            switch (type) {
                case BAN:
                    System.out.println("yo banning that dude");
                    guild.getController().ban(punished, ((BanOptions)options).getDays(), reason).queue(success, error);
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
        });
    }

}
