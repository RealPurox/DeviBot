package me.purox.devi.commands.handler;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public interface CommandExecutor {

    void execute(String[] args, Command command, CommandSender sender);

    boolean guildOnly();

    int getDescriptionTranslationID();

    List<String> getAliases();

    Permission getPermission();

}
