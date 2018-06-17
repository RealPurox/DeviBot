package me.purox.devi.commands.handler;

import me.purox.devi.commands.dev.*;
import me.purox.devi.commands.guild.*;
import me.purox.devi.commands.guild.custom.*;
import me.purox.devi.commands.handler.impl.ConsoleCommandSenderImpl;
import me.purox.devi.commands.mod.*;
import me.purox.devi.commands.music.*;
import me.purox.devi.commands.general.*;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

public class CommandHandler {

    private Devi devi;
    private ConsoleCommandSender consoleCommandSender;
    private CommandParser parser;
    private LinkedHashMap<String, CommandExecutor> commands = new LinkedHashMap<>();
    private LinkedHashMap<String, CommandExecutor> unmodifiedCommands = new LinkedHashMap<>();

    public CommandHandler(Devi devi) {
        this.devi = devi;
        this.consoleCommandSender = new ConsoleCommandSenderImpl(devi);
        this.parser = new CommandParser(devi);

        /*
            REGISTER COMMANDS
         */

        //dev commands || only add them to commands so they don't appear in !help
        commands.put("performance", new PerformanceCommandExecutor(devi));
        commands.put("reload", new ReloadCommandExecutor(devi));
        commands.put("threadlist", new ThreadListCommandExecutor(devi));
        commands.put("test", new TestCommandExecutor(devi));
        commands.put("pmowners", new PMOwnersCommandExecutor(devi));
        commands.put("admindisable", new AdminDisableCommandExecutor(devi));
        commands.put("adminenable", new AdminEnableCommandExecutor(devi));
        commands.put("guilddata", new GuildDataCommandExecutor(devi));

        //general commands
        registerCommand("help", new HelpCommandExecutor(devi));
        registerCommand("numberfact", new NumberFactCommandExecutor(devi));
        registerCommand("chucknorris", new ChuckNorrisCommandExecutor(devi));
        registerCommand("hypixel", new HypixelCommandExecutor(devi));
        registerCommand("hive", new HiveCommandExecutor(devi));
        registerCommand("fortnite", new FortniteCommandExecutor(devi));
        registerCommand("weather", new WeatherCommandExecutor(devi));
        registerCommand("flipcoin", new FlipCoinCommandExecutor(devi));
        //guild commands
        registerCommand("settings", new SettingsCommandExecutor(devi));
        registerCommand("prefix", new PrefixCommandExecutor(devi));
        registerCommand("language", new LanguageCommandExecutor(devi));
        registerCommand("modlog", new ModLogCommandExecutor(devi));
        registerCommand("automod", new AutoModCommandExecutor(devi));
        registerCommand("musiclog", new MusicLogCommandExecutor(devi));
        registerCommand("welcome", new WelcomeCommandExecutor(devi));
        //  - twitch commands
        registerCommand("twitch", new TwitchCommandExecutor(devi));
        registerCommand("streamlist", new ListStreamCommandExecutor(devi));
        //  - mod commands
        registerCommand("ban", new BanCommandExecutor(devi));
        registerCommand("unban", new UnbanCommandExecutor(devi));
        registerCommand("mute", new MuteCommandExecutor(devi));
        registerCommand("unmute", new UnmuteCommandExecutor(devi));
        registerCommand("banlist", new BanListCommandExecutor(devi));
        registerCommand("mutelist", new MuteListCommandExecutor(devi));
        registerCommand("purge", new PurgeCommandExecutor(devi));
        //  - custom commands
        registerCommand("addcommand", new AddCustomCommandExecutor(devi));
        registerCommand("commandlist", new ListCustomCommandExecutor(devi));
        registerCommand("removecommand", new RemoveCustomCommandExecutor(devi));
        registerCommand("editcommand", new EditCustomCommandExecutor(devi));
        //  - music commands
        registerCommand("join", new JoinCommandExecutor(devi));
        registerCommand("leave", new LeaveCommandExecutor(devi));
        registerCommand("play", new PlayCommandExecutor(devi));
        registerCommand("queue", new QueueCommandExecutor(devi));
        registerCommand("pause", new PauseCommandExecutor(devi));
        registerCommand("resume", new ResumeCommandExecutor(devi));
        registerCommand("current", new CurrentCommandExecutor(devi));
        registerCommand("skip", new SkipCommandExecutor(devi));
        registerCommand("shuffle", new ShuffleCommandExecutor(devi));
        registerCommand("unshuffle", new UnShuffleCommandExecutor(devi));
        registerCommand("volume", new VolumeCommandExecutor(devi));
        registerCommand("loop", new LoopCommandExecutor(devi));
    }

    private void registerCommand(String commandName, CommandExecutor commandExecutor){
        commands.put(commandName, commandExecutor);
        unmodifiedCommands.put(commandName, commandExecutor);
        if(commandExecutor.getAliases() != null) {
            for (String alias : commandExecutor.getAliases()) {
                commands.put(alias, commandExecutor);
            }
        }
    }

    public void handleCommand(String prefix, String raw, MessageReceivedEvent event, CommandSender commandSender){
        CommandParser.CommandContainer container = parser.parseCommand(raw, event);
        CommandExecutor commandExecutor = commands.get(raw.toLowerCase().substring(prefix.length()).split( " ")[0]);

        //perms check
        if (event != null && event.getGuild() != null && commandExecutor.getPermission() != null) {
            if (!event.getMember().hasPermission(event.getTextChannel(), commandExecutor.getPermission()) && !devi.getAdmins().contains(event.getAuthor().getId())) {
                MessageUtils.sendMessageAsync(event.getChannel(), devi.getTranslation(Language.getLanguage(devi.getDeviGuild(event.getGuild().getId()).getSettings().getStringValue(GuildSettings.Settings.LANGUAGE)), 31));
                return;
            }
        }

        //guild only check
        if ((commandExecutor.guildOnly() && event != null && event.getGuild() == null) || commandExecutor.guildOnly() && commandSender.isConsoleCommandSender()) {
            commandSender.reply(":warning: **" + devi.getTranslation(Language.ENGLISH, 1) + "**");
            return;
        }

        //module check
        if (devi.getDisabledModules().contains(commandExecutor.getModuleType())) {
            commandSender.reply(devi.getTranslation(container.getCommand().getLanguage(), 377));
            return;
        }

        //all good, run the command
        devi.getLogger().log("Command '" + raw + "' executed by " + commandSender.getName() + "#" + commandSender.getDiscriminator() + " in channel-type " + (event != null ? event.getChannelType() : "UNKNOWN"));
        devi.increaseCommandsExecuted();
        commandExecutor.execute(container.getArgs(), container.getCommand(), commandSender);
    }

    public LinkedHashMap<String, CommandExecutor> getCommands() {
        return commands;
    }

    public LinkedHashMap<String, CommandExecutor> getUnmodifiedCommands() {
        return unmodifiedCommands;
    }

    public void startConsoleCommandListener() {
        NonblockingBufferedReader reader = new NonblockingBufferedReader(new BufferedReader(new InputStreamReader(System.in)));
        try {
            while (true) {
                String line = reader.readLine();
                if (line != null) {
                    CommandHandler commandHandler = devi.getCommandHandler();
                    String invoke = line.split(" ")[0].toLowerCase();
                    if (commandHandler.getCommands().containsKey(invoke)) {
                        commandHandler.handleCommand("", line, null, new CommandSender(consoleCommandSender, null));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

