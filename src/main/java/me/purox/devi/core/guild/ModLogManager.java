package me.purox.devi.core.guild;

import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.Language;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;

import org.bson.Document;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Date;

public class ModLogManager {

    private Devi devi;
    public ModLogManager(Devi devi) {
        this.devi = devi;
    }

    public void logBan(DeviGuild deviGuild, Member member, Member punisher, String reason) {
        GuildSettings settings = deviGuild.getSettings();
        if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) && settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_BANS)) {
            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(new Color(255, 45, 40));
            builder.setAuthor(devi.getTranslation(language, 69));
            builder.setDescription(devi.getTranslation(language, 180, member.getUser().getName() + "#" + member.getUser().getDiscriminator())+ Emote.BAN.get());
            builder.addField(devi.getTranslation(language, 48), reason, true);
            builder.addField(devi.getTranslation(language, 47), punisher.getUser().getName() + "#" + punisher.getUser().getDiscriminator(), true);
            builder.setThumbnail(member.getUser().getAvatarUrl());
            builder.setFooter(devi.getTranslation(language, 69), null);
            builder.setTimestamp(OffsetDateTime.now());

            deviGuild.log(builder.build());
        }
    }

    public void logKick(DeviGuild deviGuild, Member member, Member punisher, String reason) {
        GuildSettings settings = deviGuild.getSettings();
        if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) && settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_KICKS)) {
            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(new Color(255, 45, 40));
            builder.setAuthor(devi.getTranslation(language, 69));
            builder.setDescription(devi.getTranslation(language, 530, member.getUser().getName() + "#" + member.getUser().getDiscriminator())+ Emote.BAN.get());
            builder.addField(devi.getTranslation(language, 48), reason, true);
            builder.addField(devi.getTranslation(language, 47), punisher.getUser().getName() + "#" + punisher.getUser().getDiscriminator(), true);
            builder.setThumbnail(member.getUser().getAvatarUrl());
            builder.setFooter(devi.getTranslation(language, 69), null);
            builder.setTimestamp(OffsetDateTime.now());

            deviGuild.log(builder.build());
        }
    }

    public void logVoiceKick(DeviGuild deviGuild, Member member, Member punisher, VoiceChannel channel, String reason) {
        GuildSettings settings = deviGuild.getSettings();
        if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) && settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_VOICEKICKS)) {
            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(new Color(255, 45, 40));
            builder.setAuthor(devi.getTranslation(language, 69));
            builder.setDescription(devi.getTranslation(language, 570, member.getUser().getName() + "#" + member.getUser().getDiscriminator())+ "\uD83D\uDCDE");
            builder.addField(devi.getTranslation(language, 48), reason, true);
            builder.addField(devi.getTranslation(language, 47), punisher.getUser().getName() + "#" + punisher.getUser().getDiscriminator(), true);
            builder.addField(devi.getTranslation(language, 59), "`" + channel.getName() + "`", true);
            builder.setThumbnail(member.getUser().getAvatarUrl());
            builder.setFooter(devi.getTranslation(language, 69), null);
            builder.setTimestamp(OffsetDateTime.now());

            deviGuild.log(builder.build());
        }
    }

    public void logMute(DeviGuild deviGuild, Member member, String punisher, String reason) {
        GuildSettings settings = deviGuild.getSettings();
        if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) && settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_MUTES)) {
            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(new Color(49, 245, 255));
            builder.setAuthor(devi.getTranslation(language, 69));
            builder.setDescription(devi.getTranslation(language, 181, member.getUser().getName() + "#" + member.getUser().getDiscriminator())+ Emote.MUTE.get());
            builder.addField(devi.getTranslation(language, 48), reason, true);
            builder.addField(devi.getTranslation(language, 47), punisher, true);
            builder.setThumbnail(member.getUser().getAvatarUrl());
            builder.setFooter(devi.getTranslation(language, 69), null);
            builder.setTimestamp(OffsetDateTime.now());

            deviGuild.log(builder.build());
        }
    }

    public void logMessageDeleted(DeviGuild deviGuild, Message message) {
        GuildSettings settings = deviGuild.getSettings();
        if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) && settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_MESSAGE_DELETED)) {
            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(new Color(255, 146, 15));
            builder.setAuthor(devi.getTranslation(language, 69));
            builder.setDescription(devi.getTranslation(language, 185) + " :no_entry_sign:");
            builder.addField(devi.getTranslation(language, 88), message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator() + " ( " + message.getAuthor().getId() + " )", false);
            builder.addField(devi.getTranslation(language, 182), message.getContentDisplay(), false);
            builder.setFooter(devi.getTranslation(language, 69), null);
            builder.setTimestamp(OffsetDateTime.now());

            deviGuild.log(builder.build());
        }
    }

    public void logMessageEdited(DeviGuild deviGuild, Message oldMessage, Message newMessage) {
        GuildSettings settings = deviGuild.getSettings();
        if (settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) && settings.getBooleanValue(GuildSettings.Settings.MOD_LOG_MESSAGE_DELETED)) {
            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(new Color(18, 244, 0));
            builder.setAuthor(devi.getTranslation(language, 69));
            builder.setDescription(devi.getTranslation(language, 186) + " :pen_ballpoint:");
            builder.addField(devi.getTranslation(language, 88), newMessage.getAuthor().getName() + "#" + newMessage.getAuthor().getDiscriminator() + " ( " + newMessage.getAuthor().getId() + " )", false);
            builder.addField(devi.getTranslation(language, 183), oldMessage.getContentDisplay(), false);
            builder.addField(devi.getTranslation(language, 184), newMessage.getContentDisplay(), false);
            builder.setFooter(devi.getTranslation(language, 69), null);
            builder.setTimestamp(OffsetDateTime.now());

            deviGuild.log(builder.build());
        }
    }
}
