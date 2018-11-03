package me.purox.devi.commands;

import me.purox.devi.commands.admin.*;
import me.purox.devi.commands.fun.*;
import me.purox.devi.commands.game.*;
import me.purox.devi.commands.info.*;
import me.purox.devi.commands.management.*;
import me.purox.devi.commands.mod.*;
import me.purox.devi.commands.music.*;
import me.purox.devi.commands.nsfw.*;
import me.purox.devi.commands.twitch.ListStreamCommand;
import me.purox.devi.commands.twitch.TwitchCommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;

import java.util.LinkedHashMap;

public class CommandHandler {

    private Devi devi;
    private LinkedHashMap<String, ICommand> commands = new LinkedHashMap<>();
    private LinkedHashMap<String, ICommand> unmodifiedCommands = new LinkedHashMap<>();

    public CommandHandler(Devi devi) {
        this.devi = devi;

        //dev
        registerCommand(new TestCommand(devi).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPremission(null));
        registerCommand(new DisableModuleCommand(devi).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPremission(null));
        registerCommand(new EnableModuleCommand(devi).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPremission(null));
        registerCommand(new AdminStatsCommand(devi).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPremission(null));
        registerCommand(new EvalCommand(devi).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPremission(null));
        registerCommand(new GuildDataCommand(devi).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPremission(null));
        registerCommand(new PerformanceCommand(devi).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPremission(null));
        registerCommand(new RebootCommand(devi).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPremission(null));
        registerCommand(new ReloadCommand(devi).setDescriptionId(0).setGuildOnly(false).setModuleType(ModuleType.DEV).setPremission(null));
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
        //info
        registerCommand(new ChangeLogCommand(devi).setDescriptionId(491).setGuildOnly(false).setModuleType(ModuleType.INFO_COMMANDS).setPremission(null));
        registerCommand(new FeedbackCommand(devi).setDescriptionId(483).setGuildOnly(false).setModuleType(ModuleType.INFO_COMMANDS).setPremission(null));
        registerCommand(new GuildStatsCommand(devi).setDescriptionId(426).setGuildOnly(true).setModuleType(ModuleType.INFO_COMMANDS).setPremission(null));
        registerCommand(new HelpCommand(devi).setDescriptionId(38).setGuildOnly(false).setModuleType(ModuleType.INFO_COMMANDS).setPremission(null));
        registerCommand(new InviteCommand(devi).setDescriptionId(621).setGuildOnly(false).setModuleType(ModuleType.INFO_COMMANDS).setPremission(null));
        registerCommand(new PingCommand(devi).setDescriptionId(543).setGuildOnly(false).setModuleType(ModuleType.INFO_COMMANDS).setPremission(null));
        registerCommand(new StatsCommand(devi).setDescriptionId(550).setGuildOnly(false).setModuleType(ModuleType.INFO_COMMANDS).setPremission(null));
        registerCommand(new SupportCommand(devi).setDescriptionId(590).setGuildOnly(false).setModuleType(ModuleType.INFO_COMMANDS).setPremission(null));
        registerCommand(new UserInfoCommand(devi).setDescriptionId(438).setGuildOnly(false).setModuleType(ModuleType.INFO_COMMANDS).setPremission(null));
        registerCommand(new WeatherCommand(devi).setDescriptionId(400).setGuildOnly(false).setModuleType(ModuleType.INFO_COMMANDS).setPremission(null));

        //game
        registerCommand(new HiveCommand(devi).setDescriptionId(226).setGuildOnly(false).setModuleType(ModuleType.GAME_COMMANDS).setPremission(null));
        registerCommand(new HypixelCommand(devi).setDescriptionId(225).setGuildOnly(false).setModuleType(ModuleType.GAME_COMMANDS).setPremission(null));
        registerCommand(new OsuCommand(devi).setDescriptionId(512).setGuildOnly(false).setModuleType(ModuleType.GAME_COMMANDS).setPremission(null));
        registerCommand(new SteamCommand(devi).setDescriptionId(511).setGuildOnly(false).setModuleType(ModuleType.GAME_COMMANDS).setPremission(null));
        //music
        registerCommand(new CurrentCommand(devi).setDescriptionId(128).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        registerCommand(new JoinCommand(devi).setDescriptionId(99).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        registerCommand(new LeaveCommand(devi).setDescriptionId(143).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        registerCommand(new LoopCommand(devi).setDescriptionId(369).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        registerCommand(new PauseCommand(devi).setDescriptionId(122).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        registerCommand(new PlayCommand(devi).setDescriptionId(115).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        registerCommand(new QueueCommand(devi).setDescriptionId(117).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        registerCommand(new RemoveCommand(devi).setDescriptionId(460).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        registerCommand(new ShuffleCommand(devi).setDescriptionId(138).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        registerCommand(new SkipCommand(devi).setDescriptionId(131).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        registerCommand(new UnPauseCommand(devi).setDescriptionId(125).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        registerCommand(new VolumeCommand(devi).setDescriptionId(189).setGuildOnly(true).setModuleType(ModuleType.MUSIC).setPremission(null));
        //management
        registerCommand(new SettingsCommand(devi).setDescriptionId(45).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT_COMMANDS).setPremission(Permission.MANAGE_SERVER));
        registerCommand(new AutoModCommand(devi).setDescriptionId(73).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT_COMMANDS).setPremission(Permission.MANAGE_SERVER));
        registerCommand(new ModLogCommand(devi).setDescriptionId(58).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT_COMMANDS).setPremission(Permission.MANAGE_SERVER));
        registerCommand(new PrefixCommand(devi).setDescriptionId(248).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT_COMMANDS).setPremission(Permission.MANAGE_SERVER));
        registerCommand(new LanguageCommand(devi).setDescriptionId(253).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT_COMMANDS).setPremission(Permission.MANAGE_SERVER));
        registerCommand(new AddCommandCommand(devi).setDescriptionId(177).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT_COMMANDS).setPremission(Permission.MANAGE_SERVER));
        registerCommand(new EditCommandCommand(devi).setDescriptionId(228).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT_COMMANDS).setPremission(Permission.MANAGE_SERVER));
        registerCommand(new EditCommandCommand(devi).setDescriptionId(228).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT_COMMANDS).setPremission(Permission.MANAGE_SERVER));
        registerCommand(new ListCommandCommand(devi).setDescriptionId(192).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT_COMMANDS).setPremission(Permission.MANAGE_SERVER));
        registerCommand(new RemoveCommandCommand(devi).setDescriptionId(194).setGuildOnly(true).setModuleType(ModuleType.MANAGEMENT_COMMANDS).setPremission(Permission.MANAGE_SERVER));
        //moderation
        registerCommand(new BanCommand(devi).setDescriptionId(39).setGuildOnly(true).setModuleType(ModuleType.MODERATION).setPremission(Permission.BAN_MEMBERS));
        registerCommand(new KickCommand(devi).setDescriptionId(536).setGuildOnly(true).setModuleType(ModuleType.MODERATION).setPremission(Permission.KICK_MEMBERS));
        registerCommand(new MuteCommand(devi).setDescriptionId(41).setGuildOnly(true).setModuleType(ModuleType.MODERATION).setPremission(Permission.VOICE_MUTE_OTHERS));
        registerCommand(new PurgeCommand(devi).setDescriptionId(151).setGuildOnly(true).setModuleType(ModuleType.MODERATION).setPremission(Permission.MANAGE_SERVER));
        registerCommand(new UnbanCommand(devi).setDescriptionId(43).setGuildOnly(true).setModuleType(ModuleType.MODERATION).setPremission(Permission.BAN_MEMBERS));
        registerCommand(new UnmuteCommand(devi).setDescriptionId(44).setGuildOnly(true).setModuleType(ModuleType.MODERATION).setPremission(Permission.VOICE_MUTE_OTHERS));
        registerCommand(new VoiceKickCommand(devi).setDescriptionId(576).setGuildOnly(true).setModuleType(ModuleType.MODERATION).setPremission(Permission.KICK_MEMBERS));
        //twitch
        registerCommand(new TwitchCommand(devi).setDescriptionId(311).setGuildOnly(true).setModuleType(ModuleType.TWITCH).setPremission(Permission.MANAGE_SERVER));
        registerCommand(new ListStreamCommand(devi).setDescriptionId(200).setGuildOnly(true).setModuleType(ModuleType.TWITCH).setPremission(Permission.MANAGE_SERVER));
        //not save for work & wife
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
