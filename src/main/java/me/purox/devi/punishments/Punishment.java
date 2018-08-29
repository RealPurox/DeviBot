package me.purox.devi.punishments;

import me.purox.devi.core.Emote;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.punishments.options.BanOptions;
import me.purox.devi.punishments.options.Options;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.time.OffsetDateTime;
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

        GuildSettings settings = deviGuild.getSettings();
        if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) && settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_BANS)) {
            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(new Color(255, 68, 82));
            builder.setAuthor(deviGuild.getDevi().getTranslation(language, 69));
            builder.setDescription(deviGuild.getDevi().getTranslation(language, 180, punished.getUser().getName() + "#" + punished.getUser().getDiscriminator() + " (" + punished.getAsMention() + ")"));
            builder.addField(deviGuild.getDevi().getTranslation(language, 48), reason, true);
            builder.addField(deviGuild.getDevi().getTranslation(language, 47), punisher.getUser().getName() + "#" + punisher.getUser().getDiscriminator() + " (" + punisher.getAsMention() + ")", true);
            builder.setThumbnail(punished.getUser().getAvatarUrl());
            builder.setTimestamp(OffsetDateTime.now());

            MessageUtils.sendMessageAsync(logChannel, builder.build(), msg -> {
                execute(guild, success, error);
                this.messageId = msg.getId();
            });
        } else {
            execute(guild, success, error);
        }
    }

    private void execute(Guild guild, Consumer<? super Void> success, Consumer<? super Throwable> error) {
        switch (type) {
            case BAN:
                System.out.println("yo banning that dude");
                //note this might fail
                MessageUtils.sendPrivateMessageAsync(punished.getUser(), Emote.INFO + " | " + deviGuild.getDevi().getTranslation(Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE)), 17, "`" + guild.getName() + "`", "\"" + reason + "\""));
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
    }
}
