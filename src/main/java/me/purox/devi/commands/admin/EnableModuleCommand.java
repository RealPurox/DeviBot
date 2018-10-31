package me.purox.devi.commands.admin;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnableModuleCommand extends ICommand {

    private Devi devi;

    public EnableModuleCommand(Devi devi) {
        super("adminenable");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        if (command.getArgs().length == 0) {
            sender.reply("Invalid amount of arguments provided. Correct usage: `" + command.getPrefix() + "adminenable [--list, --all, <module type>]`");
            return;
        }

        if (command.getArgs()[0].equalsIgnoreCase("--list")) {
            if (devi.getDisabledModules().size() == 0) {
                sender.reply("All modules are currently enabled. If one module does not work in a specific guild, it's most likely that they have disabled that module in their guild :)");
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
            sender.reply("Specified module not found. Use `" + command.getPrefix() + "adminenable --all` to get a list of all modules.");
            return;
        }

        if (!devi.getDisabledModules().contains(moduleType)) {
            sender.reply("Specified module is already enabled");
            return;
        }

        if (devi.getDisabledModules().remove(moduleType)) {
            sender.reply("Specified module has been enabled");
        } else {
            sender.reply("Something went wrong while attempting to enable the specified module");
        }

    }
}
