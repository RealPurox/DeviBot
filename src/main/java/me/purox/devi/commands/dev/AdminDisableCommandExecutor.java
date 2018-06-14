package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDisableCommandExecutor implements CommandExecutor {

    private Devi devi;

    public AdminDisableCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!devi.getAdmins().contains(sender.getId()) && !sender.isConsoleCommandSender()) return;

        if (args.length == 0) {
            sender.reply("Invalid amount of arguments provided. Correct usage: `" + command.getPrefix() + "admindisable [--list, --all, <module type>]`");
            return;
        }

        if (args[0].equalsIgnoreCase("--list")) {
            if (devi.getDisabledModules().size() == 0) {
                sender.reply("All modules are currently enabled. If one module does not work in a specific guild, it's most likely that they have disabled that module in their guild.");
                return;
            }
            sender.reply("Globally disabled modules: `" + (devi.getDisabledModules().stream().map(ModuleType::getName)).collect(Collectors.joining("`, `")) + "`");
            return;
        }

        if (args[0].equalsIgnoreCase("--all")) {
            sender.reply("All modules: `" + (Arrays.stream(ModuleType.values()).map(ModuleType::name).collect(Collectors.joining("`, `"))) + "`");
            return;
        }

        ModuleType moduleType = ModuleType.getByName(Arrays.stream(args).collect(Collectors.joining(" ")));
        if (moduleType == null) {
            sender.reply("Specified module not found. Use `" + command.getPrefix() + "admindisable --all` to get a list of all modules.");
            return;
        }

        if (devi.getDisabledModules().contains(moduleType)) {
            sender.reply("Specified module is already disabled");
            return;
        }

        if (!moduleType.canBeDisabled()) {
            sender.reply("Specified module can't be disabled");
            return;
        }

        if (devi.getDisabledModules().add(moduleType)) {
            sender.reply("Specified module has been disabled");
        } else {
            sender.reply("Something went wrong while attempting to disable the specified module");
        }
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 0;
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
        return ModuleType.DEV;
    }
}
