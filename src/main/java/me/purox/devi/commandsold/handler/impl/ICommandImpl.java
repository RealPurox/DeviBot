package me.purox.devi.commandsold.handler.impl;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ICommandImpl implements ICommand {

    private MessageReceivedEvent event;
    private DeviGuild deviGuild;
    private Language language;
    private String prefix;

    public ICommandImpl(Devi devi, MessageReceivedEvent event) {
        this.event = event;
        this.deviGuild = event.getGuild() == null ? null : devi.getDeviGuild(event.getGuild().getId());
        this.language = event.getGuild() == null ? Language.ENGLISH : Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        this.prefix = event.getGuild() == null ? devi.getSettings().getDefaultPrefix() : deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);
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
