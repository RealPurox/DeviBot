package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.entities.Message;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.TimeUnit;

public class ModLogListener extends ListenerAdapter {

    private Devi devi;
    ExpiringMap<String, Message> messages;

    public ModLogListener(Devi devi) {
        this.devi = devi;
        this.messages = ExpiringMap.builder().variableExpiration().build();
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getGuild() == null || event.getMember() == null) return;
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

        messages.put(event.getGuild().getId(), event.getMessage(), ExpirationPolicy.CREATED, 15, TimeUnit.MINUTES);
    }
}
