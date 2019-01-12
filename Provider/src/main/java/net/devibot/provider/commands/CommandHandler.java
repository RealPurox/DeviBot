package net.devibot.provider.commands;

import net.devibot.core.entities.DeviGuild;
import net.devibot.provider.commands.dev.EvalCommand;
import net.devibot.provider.commands.dev.GuildDataCommand;
import net.devibot.provider.commands.dev.PerformanceCommand;
import net.devibot.provider.commands.dev.TestCommand;
import net.devibot.provider.commands.management.LanguageCommand;
import net.devibot.provider.commands.management.PrefixCommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.entities.Emote;
import net.devibot.provider.entities.Language;
import net.devibot.provider.entities.ModuleType;
import net.devibot.provider.utils.MessageUtils;
import net.devibot.provider.utils.Translator;
import net.dv8tion.jda.core.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedHashMap;

public class CommandHandler {

    private Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    private DiscordBot discordBot;
    private LinkedHashMap<String, ICommand> commands = new LinkedHashMap<>();
    private LinkedHashMap<String, ICommand> unmodifiedCommands = new LinkedHashMap<>();

    public CommandHandler(DiscordBot discordBot) {
        this.discordBot  = discordBot;

        //register commands here

        //DEV COMMANDS
        registerCommand(new TestCommand(discordBot).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPermission(null));
        registerCommand(new EvalCommand(discordBot).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPermission(null));
        registerCommand(new GuildDataCommand(discordBot).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPermission(null));
        registerCommand(new PerformanceCommand(discordBot).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPermission(null));

        //MANAGEMENT COMMANDS
        registerCommand(new PrefixCommand(discordBot).setDescriptionId(248).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT).setPermission(Permission.MANAGE_SERVER));
        registerCommand(new LanguageCommand(discordBot).setDescriptionId(253).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT).setPermission(Permission.MANAGE_SERVER));
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

        //dev command
        if (iCommand.getModuleType() == ModuleType.DEV && !Arrays.asList(discordBot.getConfig().getDevelopers()).contains(command.getAuthor().getId())) return;

        //perms check
        if (command.getGuild() != null && iCommand.getPermission() != null) {
            if (!command.getMember().hasPermission(command.getTextChannel(), iCommand.getPermission())) { //todo admins
                DeviGuild deviGuild = discordBot.getCacheManager().getDeviGuildCache().getDeviGuild(command.getGuild().getId());
                MessageUtils.sendMessage(command.getTextChannel(), Emote.ERROR + " | " + Translator.getTranslation(Language.getLanguage(deviGuild.getLanguage()), 31));
                return;
            }
        }

        //guild only check
        if (iCommand.isGuildOnly() && command.getGuild() == null) {
            MessageUtils.sendMessage(command.getTextChannel(), Emote.ERROR + " | " + Translator.getTranslation(Language.ENGLISH, 1));
            return;
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
