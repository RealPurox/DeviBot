package me.purox.devi.commands.handler;

import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public interface CommandExecutor {

    void execute(String[] args, Command command, CommandSender sender);

    boolean guildOnly();

    int getDescriptionTranslationID();

    List<String> getAliases();

    Permission getPermission();

    ModuleType getModuleType();
}
