package me.purox.devi.commandsold.handler;

import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface ICommand {

    DeviGuild getDeviGuild();

    Language getLanguage();

    String getPrefix();

    MessageReceivedEvent getEvent();
}
