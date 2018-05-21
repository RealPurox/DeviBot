package me.purox.devi.commands.handler;

import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Command {

    DeviGuild getDeviGuild();

    Language getLanguage();

    String getPrefix();

    MessageReceivedEvent getEvent();
}
