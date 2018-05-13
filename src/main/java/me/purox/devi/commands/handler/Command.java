package me.purox.devi.commands.handler;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public interface Command {

    void execute(String[] args, MessageReceivedEvent event, CommandSender sender);

    boolean guildOnly();

    int getDescriptionTranslationID();

    List<String> getAliases();

    Permission getPermission();

}
