package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.entities.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.entities.Message;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

public class ModLogListener extends ListenerAdapter {

    private Devi devi;
    private ExpiringMap<String, Message> messages;
    private ExpiringMap<String, String> banned;

    public ModLogListener(Devi devi) {
        this.devi = devi;
        this.messages = ExpiringMap.builder().variableExpiration().build();
        this.banned = ExpiringMap.builder().variableExpiration().build();
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        banned.put(event.getUser().getId(), "", ExpirationPolicy.CREATED, 5, TimeUnit.MINUTES);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getGuild() == null || event.getMember() == null) return;
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());

        if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) &&
                (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MOD_LOG_MESSAGE_DELETED) ||
                        deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MOD_LOG_MESSAGE_EDITED))) {
            messages.put(event.getGuild().getId() + "|" + event.getMessage().getId(), event.getMessage(), ExpirationPolicy.CREATED, 15, TimeUnit.MINUTES);
        }
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        if (event.getGuild() == null) return;
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());

        if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) && deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MOD_LOG_MESSAGE_DELETED)) {
            Message message = messages.get(event.getGuild().getId() + "|" + event.getMessageId());
            if (message == null || message.getContentDisplay().equals("") || banned.containsKey(message.getAuthor().getId()) ||
                    devi.getPrunedMessages().containsKey(message.getId()) || message.getAuthor().isBot()) return;
            logMessageDeleted(deviGuild, message);
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (event.getGuild() == null) return;
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());

        if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) && deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MOD_LOG_MESSAGE_EDITED)) {
            Message old = messages.get(event.getGuild().getId() + "|" + event.getMessageId());
            Message newMessage = event.getMessage();
            if (old == null || old.getContentDisplay().equals("") || devi.getPrunedMessages().containsKey(event.getMessageId()) || newMessage.getAuthor().isBot()) return;
            messages.remove(event.getGuild().getId() + "|" + event.getMessageId());
            messages.put(event.getGuild().getId() + "|" + event.getMessageId(), event.getMessage(), ExpirationPolicy.CREATED, 15, TimeUnit.MINUTES);
            logMessageEdited(deviGuild, old, newMessage);
        }
    }

    private void logMessageDeleted(DeviGuild deviGuild, Message message) {
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
    private void logMessageEdited(DeviGuild deviGuild, Message oldMessage, Message newMessage) {
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
