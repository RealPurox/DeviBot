package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class HelpCommandExecutor implements CommandExecutor {

    private Devi devi;

    public HelpCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        HashMap<String, CommandExecutor> commands = devi.getCommandHandler().getUnmodifiedCommands();
        if (args.length == 0 ) {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setAuthor("Devi Command Help", "https://www.devibot.net/wiki");
            builder.setColor(Color.decode("#7289da"));

            builder.appendDescription("Use `" + command.getPrefix() + "help <command>` to get information about a specific command.\n");
            builder.appendDescription("Example: `" + command.getPrefix() + "help settings`\n\n");
            builder.appendDescription("Use `" + command.getPrefix() + "modulehelp <module>` to get information about a specific module.\n");
            builder.appendDescription("Example: `" + command.getPrefix() + "modulehelp music`");

            for (ModuleType moduleType : ModuleType.values()) {
                if (devi.getDisabledModules().contains(moduleType))
                    builder.addField(moduleType.getName(), "This module is currently disabled", false);
                else if (commands.keySet().stream().anyMatch(invoke -> commands.get(invoke).getModuleType() == moduleType))
                    builder.addField(moduleType.getName(), "`" + command.getPrefix() + commands.keySet().stream()
                            .filter(invoke -> commands.get(invoke).getModuleType() == moduleType)
                            .collect(Collectors.joining("`, `" + command.getPrefix())) + "`", false);
            }

            sender.reply(builder.build());
            return;
        }

        String invoke = args[0];

        if (!commands.containsKey(invoke)) {
            sender.reply(DeviEmote.ERROR.get() + " | The command `" + invoke + "` could not be found.");
            return;
        }

        char capital = Character.toUpperCase(invoke.charAt(0));
        String capitalInvoke = capital + invoke.toLowerCase().substring(1);


        CommandExecutor cmd = commands.get(invoke);
        StringBuilder builder = new StringBuilder();

        builder.append("__**").append(capitalInvoke).append(" - Command Help**__\n\n");

        if (cmd.getPermission() != null)
            builder.append("`-` This command requires you to have the ").append(cmd.getPermission().getName()).append(" permission.").append("\n\n");

        if (cmd.guildOnly()) {
            builder.append("`-` This command can only be executed in Discord servers\n\n");
        }

        builder.append("**Module:** ").append(cmd.getModuleType().getName()).append("\n\n");
        builder.append("**Description:** ").append(devi.getTranslation(command.getLanguage(), cmd.getDescriptionTranslationID())).append("\n\n");

        if (cmd.getAliases() == null)
            builder.append("**Aliases:** None");
        else builder.append("**Aliases:** ").append("`").append(command.getPrefix()).append(cmd.getAliases().stream().collect(Collectors.joining("`, `" + command.getPrefix()))).append("`").append("\n\n");



        sender.reply(builder.toString());
    }


    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 38;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.INFO_COMMANDS;
    }
}

