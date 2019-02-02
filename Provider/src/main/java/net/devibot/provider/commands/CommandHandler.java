package net.devibot.provider.commands;

import net.devibot.provider.commands.dev.EvalCommand;
import net.devibot.provider.commands.dev.GuildDataCommand;
import net.devibot.provider.commands.dev.PerformanceCommand;
import net.devibot.provider.commands.dev.TestCommand;
import net.devibot.provider.commands.dev.UserDataCommand;
import net.devibot.provider.commands.management.AutoModCommand;
import net.devibot.provider.commands.management.LanguageCommand;
import net.devibot.provider.commands.management.PrefixCommand;
import net.devibot.provider.commands.predicates.CommandModulePredicate;
import net.devibot.provider.commands.predicates.GuildOnlyPredicate;
import net.devibot.provider.commands.predicates.PermissionPredicate;
import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.entities.ModuleType;
import net.dv8tion.jda.core.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Predicate;

public class CommandHandler {

    private Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    private DiscordBot discordBot;
    private LinkedHashMap<String, ICommand> commands = new LinkedHashMap<>();
    private LinkedHashMap<String, ICommand> unmodifiedCommands = new LinkedHashMap<>();

    private List<Predicate<ICommand.Command>> predicates = new ArrayList<>();

    public CommandHandler(DiscordBot discordBot) {
        this.discordBot  = discordBot;

        //register predicates here
        predicates.add(new GuildOnlyPredicate());
        predicates.add(new CommandModulePredicate());
        predicates.add(new PermissionPredicate());

        //register commands here

        //DEV COMMANDS
        registerCommand(new TestCommand(discordBot).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPermission(null));
        registerCommand(new EvalCommand(discordBot).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPermission(null));
        registerCommand(new GuildDataCommand(discordBot).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPermission(null));
        registerCommand(new UserDataCommand(discordBot).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPermission(null));
        registerCommand(new PerformanceCommand(discordBot).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPermission(null));

        //MANAGEMENT COMMANDS
        registerCommand(new PrefixCommand(discordBot).setDescriptionId(248).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT).setPermission(Permission.MANAGE_SERVER));
        registerCommand(new LanguageCommand(discordBot).setDescriptionId(253).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT).setPermission(Permission.MANAGE_SERVER));
        registerCommand(new AutoModCommand(discordBot).setDescriptionId(73).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT).setPermission(Permission.MANAGE_SERVER));
    }

    private void registerCommand(ICommand iCommand) {
        commands.put(iCommand.getInvoke(), iCommand);
        unmodifiedCommands.put(iCommand.getInvoke(), iCommand);
        if (iCommand.getAliases() != null) {
            for (String alias : iCommand.getAliases()) {
                commands.put(alias, iCommand);
            }
        }
    }

    public void handleCommand(ICommand.Command command) {
        ICommand iCommand = command.getICommand();
        //command not registered
        if (iCommand == null) return;

        for (Predicate<ICommand.Command> predicate : predicates) {
            if (!predicate.test(command))
                return; //predicate failed
        }

        CommandSender sender = new CommandSender(command.getAuthor(), command);

        logger.info("Command '" + command.getRaw() + "' executed by " + sender.getName() + "#" + sender.getDiscriminator() + (command.getGuild() == null ? " in DMs" : " in channel #" + command.getChannel().getName() + " (" + command.getChannel().getId() + ")" + " in guild " + command.getGuild().getName() + " (" + command.getGuild().getId() + ")"));
        iCommand.execute(sender, command);
    }

    public LinkedHashMap<String, ICommand> getCommands() {
        return commands;
    }

    public LinkedHashMap<String, ICommand> getUnmodifiedCommands() {
        return unmodifiedCommands;
    }
}
