package me.purox.devi.core.guild;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

import java.awt.*;
import java.time.OffsetDateTime;

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
            builder.setColor(new Color(34, 113, 126));
            builder.setAuthor(devi.getTranslation(language, 69));
            builder.setDescription(member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " was banned " + DeviEmote.BAN.get());
            builder.addField(devi.getTranslation(language, 48), reason, true);
            builder.addField(devi.getTranslation(language, 47), punisher.getUser().getName() + "#" + punisher.getUser().getDiscriminator(), true);
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
            builder.setColor(new Color(34, 113, 126));
            builder.setAuthor(devi.getTranslation(language, 69));
            builder.setDescription(member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " has been muted " + DeviEmote.MUTE.get());
            builder.addField(devi.getTranslation(language, 48), reason, true);
            builder.addField(devi.getTranslation(language, 47), punisher, true);
            builder.setThumbnail(member.getUser().getAvatarUrl());
            builder.setFooter(devi.getTranslation(language, 69), null);
            builder.setTimestamp(OffsetDateTime.now());

            deviGuild.log(builder.build());
        }
    }
}
