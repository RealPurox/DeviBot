package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.MessageUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.entities.Message;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.TimeUnit;

public class ModLogListener extends ListenerAdapter {

    private Devi devi;
    private ExpiringMap<String, Message> messages;

    public ModLogListener(Devi devi) {
        this.devi = devi;
        this.messages = ExpiringMap.builder().variableExpiration().build();
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

        if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) &&
                deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MOD_LOG_MESSAGE_DELETED)) {
            Message message = messages.get(event.getGuild().getId() + "|" + event.getMessageId());
            if (message != null && !message.getContentDisplay().equals("")) devi.getModLogManager().logMessageDeleted(deviGuild, message);
        }
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if (event.getGuild() == null) return;
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());

        if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED) &&
                deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MOD_LOG_MESSAGE_EDITED)) {
            Message old = messages.get(event.getGuild().getId() + "|" + event.getMessageId());
            Message newMessage = event.getMessage();
            if (old != null && !old.getContentDisplay().equals("")) {
                messages.remove(event.getGuild().getId() + "|" + event.getMessageId());
                messages.put(event.getGuild().getId() + "|" + event.getMessageId(), event.getMessage(), ExpirationPolicy.CREATED, 15, TimeUnit.MINUTES);
                devi.getModLogManager().logMessageEdited(deviGuild, old, newMessage);
            }
        }
    }
}
