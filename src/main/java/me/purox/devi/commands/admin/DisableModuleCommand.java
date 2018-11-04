package me.purox.devi.commands.admin;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.ModuleType;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DisableModuleCommand extends ICommand {

    private Devi devi;

    public DisableModuleCommand(Devi devi) {
        super("admindisable");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        if (command.getArgs().length == 0) {
            sender.reply("Invalid amount of arguments provided. Correct usage: `" + command.getPrefix() + "admindisable [--list, --all, <module type>]`");
            return;
        }

        if (command.getArgs()[0].equalsIgnoreCase("--list")) {
            if (devi.getDisabledModules().size() == 0) {
                sender.reply("All modules are currently enabled. If one module does not work in a specific guild, it's most likely that they have disabled that module in their guild.");
                return;
            }
            sender.reply("Globally disabled modules: `" + (devi.getDisabledModules().stream().map(ModuleType::getName)).collect(Collectors.joining("`, `")) + "`");
            return;
        }

        if (command.getArgs()[0].equalsIgnoreCase("--all")) {
            sender.reply("All modules: `" + (Arrays.stream(ModuleType.values()).map(ModuleType::name).collect(Collectors.joining("`, `"))) + "`");
            return;
        }

        ModuleType moduleType = ModuleType.getByName(String.join(" ", command.getArgs()));
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
}
