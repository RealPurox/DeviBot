package me.purox.devi.punishments;

import me.purox.devi.core.Emote;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.punishments.options.BanOptions;
import me.purox.devi.punishments.options.MuteOptions;
import me.purox.devi.punishments.options.Options;
import me.purox.devi.punishments.options.VoiceKickOptions;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;
import org.bson.Document;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

public class Punishment {

    public enum Type {
        BAN, MUTE, KICK, VOICEKICK, UNBAN, UNMUTE
    }

    private DeviGuild deviGuild; //-
    private int caseId; //-
    private long time; //-
    private Punishment.Type type; //*
    private User punisher; //*
    private User punished; //*
    private String reason; //*
    private String messageId; //TODO
    private String channelId; //TODO
    private Options options;

    Punishment(DeviGuild deviGuild, int caseId, long time, Type type, User punisher, User punished, String reason, Options options) {
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

    private void saveLog() {
        Document document = new Document()
                .append("guild", this.deviGuild.getId())
                .append("case", this.caseId)
                .append("time", this.time)
                .append("type", this.type.name())
                .append("punisher", this.punisher.getId())
                .append("punished", this.punished.getId())
                .append("reason", this.reason)
                .append("message", this.messageId)
                .append("channel", this.channelId);
        deviGuild.getDevi().getDatabaseManager().saveToDatabase("punishments", document);
    }

    public void execute(Consumer<? super Void> success, Consumer<? super Throwable> error) {
        Guild guild = deviGuild.getDevi().getShardManager().getGuildById(deviGuild.getId());
        if (guild == null) {
            error.accept(new Throwable());
            return;
        }

        TextChannel logChannel = guild.getTextChannelById(deviGuild.getSettings().getStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL));
        if (logChannel == null) {
            execute(guild, success, error);
            return;
        }

        this.channelId = logChannel.getId();

        GuildSettings settings = deviGuild.getSettings();

        if (!settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED)) {
            execute(guild, success, error);
            return;
        }

        switch (type) {
            case BAN:
                if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_BANS)) {
                    Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(new Color(255, 68, 82));
                    builder.setAuthor(deviGuild.getDevi().getTranslation(language, 69) + " (" + deviGuild.getDevi().getTranslation(language, 619, String.valueOf(this.caseId)) + ")");
                    builder.setDescription(deviGuild.getDevi().getTranslation(language, 596,
                            punished.getName() + "#" + punished.getDiscriminator() + " (" + punished.getAsMention() + ")"));
                    builder.addField(deviGuild.getDevi().getTranslation(language, 48), reason, true);
                    builder.addField(deviGuild.getDevi().getTranslation(language, 47),
                            punisher.getName() + "#" + punisher.getDiscriminator() + " (" + punisher.getAsMention() + ")", true);
                    builder.setThumbnail(punished.getAvatarUrl());
                    builder.setTimestamp(OffsetDateTime.now());

                    MessageUtils.sendMessageAsync(logChannel, builder.build(), msg -> {
                        this.messageId = msg.getId();
                        saveLog();
                        execute(guild, success, error);
                    });
                } else execute(guild, success, error);
                break;
            case UNBAN:
                if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_BANS)) {
                    Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(new Color(71, 255, 38));
                    builder.setAuthor(deviGuild.getDevi().getTranslation(language, 69) + " (" + deviGuild.getDevi().getTranslation(language, 619, String.valueOf(this.caseId)) + ")");
                    builder.setDescription(deviGuild.getDevi().getTranslation(language, 600,
                            punished.getName() + "#" + punished.getDiscriminator() + " (" + punished.getAsMention() + ")"));
                    builder.addField(deviGuild.getDevi().getTranslation(language, 48), reason, true);
                    builder.addField(deviGuild.getDevi().getTranslation(language, 602),
                            punisher.getName() + "#" + punisher.getDiscriminator() + " (" + punisher.getAsMention() + ")", true);
                    builder.setThumbnail(punished.getAvatarUrl());
                    builder.setTimestamp(OffsetDateTime.now());

                    MessageUtils.sendMessageAsync(logChannel, builder.build(), msg -> {
                        this.messageId = msg.getId();
                        saveLog();
                        execute(guild, success, error);
                    });
                } else execute(guild, success, error);
                break;
            case KICK:
                if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_KICKS)) {
                    Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(new Color(255, 249, 58));
                    builder.setAuthor(deviGuild.getDevi().getTranslation(language, 69) + " (" + deviGuild.getDevi().getTranslation(language, 619, String.valueOf(this.caseId)) + ")");
                    builder.setDescription(deviGuild.getDevi().getTranslation(language, 606,
                            punished.getName() + "#" + punished.getDiscriminator() + " (" + punished.getAsMention() + ")"));
                    builder.addField(deviGuild.getDevi().getTranslation(language, 48), reason, true);
                    builder.addField(deviGuild.getDevi().getTranslation(language, 47),
                            punisher.getName() + "#" + punisher.getDiscriminator() + " (" + punisher.getAsMention() + ")", true);
                    builder.setThumbnail(punished.getAvatarUrl());
                    builder.setTimestamp(OffsetDateTime.now());

                    MessageUtils.sendMessageAsync(logChannel, builder.build(), msg -> {
                        this.messageId = msg.getId();
                        saveLog();
                        execute(guild, success, error);
                    });
                } else execute(guild, success, error);
                break;
            case MUTE:
                if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_MUTES)) {
                    Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(new Color(255, 141, 36));
                    builder.setAuthor(deviGuild.getDevi().getTranslation(language, 69) + " (" + deviGuild.getDevi().getTranslation(language, 619, String.valueOf(this.caseId)) + ")");
                    builder.setDescription(deviGuild.getDevi().getTranslation(language, 181,
                            punished.getName() + "#" + punished.getDiscriminator() + " (" + punished.getAsMention() + ")"));
                    builder.addField(deviGuild.getDevi().getTranslation(language, 48), reason, true);
                    builder.addField(deviGuild.getDevi().getTranslation(language, 47),
                            punisher.getName() + "#" + punisher.getDiscriminator() + " (" + punisher.getAsMention() + ")", true);
                    builder.setThumbnail(punished.getAvatarUrl());
                    builder.setTimestamp(OffsetDateTime.now());

                    MessageUtils.sendMessageAsync(logChannel, builder.build(), msg -> {
                        this.messageId = msg.getId();
                        saveLog();
                        execute(guild, success, error);
                    });
                } else execute(guild, success, error);
                break;
            case UNMUTE:
                if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_MUTES)) {
                    Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(new Color(93, 242, 255));
                    builder.setAuthor(deviGuild.getDevi().getTranslation(language, 69) + " (" + deviGuild.getDevi().getTranslation(language, 619, String.valueOf(this.caseId)) + ")");
                    builder.setDescription(deviGuild.getDevi().getTranslation(language, 611,
                            punished.getName() + "#" + punished.getDiscriminator() + " (" + punished.getAsMention() + ")"));
                    builder.addField(deviGuild.getDevi().getTranslation(language, 48), reason, true);
                    builder.addField(deviGuild.getDevi().getTranslation(language, 47),
                            punisher.getName() + "#" + punisher.getDiscriminator() + " (" + punisher.getAsMention() + ")", true);
                    builder.setThumbnail(punished.getAvatarUrl());
                    builder.setTimestamp(OffsetDateTime.now());

                    MessageUtils.sendMessageAsync(logChannel, builder.build(), msg -> {
                        this.messageId = msg.getId();
                        saveLog();
                        execute(guild, success, error);
                    });
                } else execute(guild, success, error);
                break;
            case VOICEKICK:
                if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_KICKS)) {
                    Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(new Color(255, 249, 58));
                    builder.setAuthor(deviGuild.getDevi().getTranslation(language, 69) + " (" + deviGuild.getDevi().getTranslation(language, 619, String.valueOf(this.caseId)) + ")");
                    builder.setDescription(deviGuild.getDevi().getTranslation(language, 618,
                            punished.getName() + "#" + punished.getDiscriminator() + " (" + punished.getAsMention() + ")", ((VoiceKickOptions)this.options).getChannel().getName()));
                    builder.addField(deviGuild.getDevi().getTranslation(language, 48), reason, true);
                    builder.addField(deviGuild.getDevi().getTranslation(language, 47),
                            punisher.getName() + "#" + punisher.getDiscriminator() + " (" + punisher.getAsMention() + ")", true);
                    builder.setThumbnail(punished.getAvatarUrl());
                    builder.setTimestamp(OffsetDateTime.now());

                    MessageUtils.sendMessageAsync(logChannel, builder.build(), msg -> {
                        this.messageId = msg.getId();
                        saveLog();
                        execute(guild, success, error);
                    });
                } else execute(guild, success, error);
                break;
            default:
                break;
        }
    }

    private void execute(Guild guild, Consumer<? super Void> success, Consumer<? super Throwable> error) {
        switch (type) {
            case BAN:
                //note this might fail
                MessageUtils.sendPrivateMessageAsync(punished, Emote.INFO + " | " + deviGuild.getDevi().getTranslation(Language.getLanguage(deviGuild.getSettings()
                        .getStringValue(GuildSettings.Settings.LANGUAGE)), 17, "`" + guild.getName() + "`", "\"" + reason + "\""));
                guild.getController().ban(punished, ((BanOptions)options).getDays(), reason).queue(success, error);
                break;
            case UNBAN:
                guild.getController().unban(punished).queue(success, error);
                break;
            case KICK:
                //note this might fail
                MessageUtils.sendPrivateMessageAsync(punished, Emote.INFO + " | " + deviGuild.getDevi().getTranslation(Language.getLanguage(deviGuild.getSettings()
                        .getStringValue(GuildSettings.Settings.LANGUAGE)), 532, "`" + guild.getName() + "`", "\"" + reason + "\""));
                guild.getController().kick(punished.getId(), reason).queue(success, error);
                break;
            case MUTE:
                //note this might fail
                MessageUtils.sendPrivateMessageAsync(punished, Emote.INFO + " | " + deviGuild.getDevi().getTranslation(Language.getLanguage(deviGuild.getSettings()
                        .getStringValue(GuildSettings.Settings.LANGUAGE)), 609, "`" + guild.getName() + "`", "\"" + reason + "\""));
                guild.getController().addSingleRoleToMember(guild.getMember(punished), ((MuteOptions)options).getRole()).queue(success, error);
                break;
            case UNMUTE:
                //note this might fail
                MessageUtils.sendPrivateMessageAsync(punished, Emote.INFO + " | " + deviGuild.getDevi().getTranslation(Language.getLanguage(deviGuild.getSettings()
                        .getStringValue(GuildSettings.Settings.LANGUAGE)), 613, "`" + guild.getName() + "`"));
                guild.getController().removeSingleRoleFromMember(guild.getMember(punished), ((MuteOptions)options).getRole()).queue(success, error);
                break;
            case VOICEKICK:
                guild.getController().createVoiceChannel("voice-kick").queue(channel -> {
                    guild.getController().moveVoiceMember(guild.getMember(punished), (VoiceChannel) channel).queue(moved -> {
                        channel.delete().queue(success, error);
                    }, error);
                }, error);
                break;
        }
    }
}