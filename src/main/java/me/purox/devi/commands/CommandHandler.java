package me.purox.devi.commands;

import me.purox.devi.commands.admin.TestCommand;
import me.purox.devi.commands.fun.*;
import me.purox.devi.commands.game.HiveCommand;
import me.purox.devi.commands.game.HypixelCommand;
import me.purox.devi.commands.game.OsuCommand;
import me.purox.devi.commands.game.SteamCommand;
import me.purox.devi.commands.nsfw.UrbanDictionaryCommand;
import me.purox.devi.commandsold.general.UrbanDictionaryCommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;

import java.util.LinkedHashMap;

public class CommandHandler {

    private Devi devi;
    private LinkedHashMap<String, ICommand> commands = new LinkedHashMap<>();
    private LinkedHashMap<String, ICommand> unmodifiedCommands = new LinkedHashMap<>();

    public CommandHandler(Devi devi) {
        this.devi = devi;

        //dev
        registerCommand(new TestCommand(devi).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPremission(null));
        //fun
        registerCommand(new CatCommand(devi).setDescriptionId(541).setGuildOnly(false).setModuleType(ModuleType.FUN_COMMANDS).setPremission(null));
        registerCommand(new CatFactCommand(devi).setDescriptionId(537).setGuildOnly(false).setModuleType(ModuleType.FUN_COMMANDS).setPremission(null));
        registerCommand(new ChristmasCommand(devi).setDescriptionId(593).setGuildOnly(false).setModuleType(ModuleType.FUN_COMMANDS).setPremission(null));
        registerCommand(new ChuckNorrisCommand(devi).setDescriptionId(218).setGuildOnly(false).setModuleType(ModuleType.FUN_COMMANDS).setPremission(null));
        registerCommand(new DogCommand(devi).setDescriptionId(539).setGuildOnly(false).setModuleType(ModuleType.FUN_COMMANDS).setPremission(null));
        registerCommand(new DogFactCommand(devi).setDescriptionId(542).setGuildOnly(false).setModuleType(ModuleType.FUN_COMMANDS).setPremission(null));
        registerCommand(new FlipCoinCommand(devi).setDescriptionId(401).setGuildOnly(false).setModuleType(ModuleType.FUN_COMMANDS).setPremission(null));
        registerCommand(new GoogleCommand(devi).setDescriptionId(579).setGuildOnly(false).setModuleType(ModuleType.FUN_COMMANDS).setPremission(null));
        registerCommand(new NumberFactCommand(devi).setDescriptionId(216).setGuildOnly(false).setModuleType(ModuleType.FUN_COMMANDS).setPremission(null));
        registerCommand(new RandomColorCommand(devi).setDescriptionId(404).setGuildOnly(false).setModuleType(ModuleType.FUN_COMMANDS).setPremission(null));
        //game
        registerCommand(new HiveCommand(devi).setDescriptionId(226).setGuildOnly(false).setModuleType(ModuleType.GAME_COMMANDS).setPremission(null));
        registerCommand(new HypixelCommand(devi).setDescriptionId(225).setGuildOnly(false).setModuleType(ModuleType.GAME_COMMANDS).setPremission(null));
        registerCommand(new OsuCommand(devi).setDescriptionId(512).setGuildOnly(false).setModuleType(ModuleType.GAME_COMMANDS).setPremission(null));
        registerCommand(new SteamCommand(devi).setDescriptionId(511).setGuildOnly(false).setModuleType(ModuleType.GAME_COMMANDS).setPremission(null));
        //nsfw
        registerCommand(new UrbanDictionaryCommand(devi).setDescriptionId(568).setGuildOnly(false).setModuleType(ModuleType.NSFW_COMMANDS).setPremission(null));
    }

    private void registerCommand(ICommand commandExecutor){
        commands.put(commandExecutor.getInvoke(), commandExecutor);
        unmodifiedCommands.put(commandExecutor.getInvoke(), commandExecutor);
        if(commandExecutor.getAliases() != null) {
            for (String alias : commandExecutor.getAliases()) {
                commands.put(alias, commandExecutor);
            }
        }
    }

    public void handleCommand(ICommand.Command command) {
        ICommand iCommand = command.getICommand();
        //command not registered
        if (iCommand == null) return;

        CommandSender sender = new CommandSender(command.getAuthor(), command);

        //perms check
        if (command.getGuild() != null && iCommand.getPermission() != null) {
            if (!command.getMember().hasPermission(command.getTextChannel(), iCommand.getPermission()) && !devi.getAdmins().contains(sender.getId())) {
                MessageUtils.sendMessageAsync(command.getChannel(), devi.getTranslation(Language.getLanguage(devi.getDeviGuild(command.getGuild().getId()).getSettings().getStringValue(GuildSettings.Settings.LANGUAGE)), 31));
                return;
            }
        }

        //guild only check
        if ((iCommand.isGuildOnly() && command.getGuild() == null)) {
            sender.reply(":warning: **" + devi.getTranslation(Language.ENGLISH, 1) + "**");
            return;
        }

        //module check
        if (devi.getDisabledModules().contains(iCommand.getModuleType())) {
            sender.reply(devi.getTranslation(command.getLanguage(), 377));
            return;
        }

        //all good, run the command
        devi.getLogger().log("Command '" + command.getRaw() + "' executed by " + sender.getName() + "#" + sender.getDiscriminator() +
                (command.getGuild() == null ? " in DMs" : " in channel #" + command.getChannel().getName() + " (" + command.getChannel().getId() + "), " +
                        "guild " + command.getGuild().getName() + " (" + command.getGuild().getId() + ")"));
        devi.increaseCommandsExecuted();
        iCommand.execute(sender, command);
    }

    public LinkedHashMap<String, ICommand> getUnmodifiedCommands() {
        return unmodifiedCommands;
    }

    public LinkedHashMap<String, ICommand> getCommands() {
        return commands;
    }
}
