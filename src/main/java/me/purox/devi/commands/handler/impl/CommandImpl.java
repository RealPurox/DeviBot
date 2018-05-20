package me.purox.devi.commands.handler.impl;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandImpl implements Command {

    private MessageReceivedEvent event;
    private DeviGuild deviGuild;
    private Language language;
    private String prefix;

    public CommandImpl (Devi devi, MessageReceivedEvent event) {
        this.event = event;
        this.deviGuild = devi.getDeviGuild(event.getGuild().getId());
        this.language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        this.prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);
    }

    @Override
    public DeviGuild getDeviGuild() {
        return deviGuild;
    }

    @Override
    public Language getLanguage() {
        return language;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public MessageReceivedEvent getEvent() {
        return event;
    }
}
