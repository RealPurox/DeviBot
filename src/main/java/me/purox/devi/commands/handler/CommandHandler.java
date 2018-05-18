package me.purox.devi.commands.handler;

import me.purox.devi.commands.dev.PerformanceCommand;
import me.purox.devi.commands.dev.ReloadCommand;
import me.purox.devi.commands.guild.AutoModCommand;
import me.purox.devi.commands.guild.custom.AddCommand;
import me.purox.devi.commands.info.HelpCommand;
import me.purox.devi.commands.mod.*;
import me.purox.devi.commands.guild.SettingsCommand;
import me.purox.devi.commands.guild.ModLogCommand;
import me.purox.devi.commands.music.*;
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
    private LinkedHashMap<String, Command> commands = new LinkedHashMap<>();
    private LinkedHashMap<String, Command> unmodifiedCommands = new LinkedHashMap<>();

    public CommandHandler(Devi devi) {
        this.devi = devi;
        this.consoleCommandSender = new ConsoleCommandSenderImpl(devi);

        /*
            REGISTER COMMANDS
         */

        //dev commands || only add them to commands so they don't appear in !help
        commands.put("performance", new PerformanceCommand(devi));
        commands.put("reload", new ReloadCommand(devi));

        //info commands
        registerCommand("help", new HelpCommand(devi));

        //guild commands
        registerCommand("settings", new SettingsCommand(devi));
        //registerCommand("embed", new EmbedCommand(devi));
        registerCommand("modlog", new ModLogCommand(devi));
        registerCommand("automod", new AutoModCommand(devi));
        //  - mod commands
        registerCommand("ban", new BanCommand(devi));
        registerCommand("unban", new UnbanCommand(devi));
        registerCommand("mute", new MuteCommand(devi));
        registerCommand("unmute", new UnmuteCommand(devi));
        registerCommand("banlist", new BanlistCommand(devi));
        registerCommand("mutelist", new MutelistCommand(devi));
        registerCommand("purge", new PurgeCommand(devi));
        //  - custom commands
        //registerCommand("addcommand", new AddCommand(devi));
        //  - music commands
        registerCommand("join", new JoinCommand(devi));
        registerCommand("leave", new LeaveCommand(devi));
        registerCommand("play", new PlayCommand(devi));
        registerCommand("queue", new QueueCommand(devi));
        registerCommand("pause", new PauseCommand(devi));
        registerCommand("resume", new ResumeCommand(devi));
        registerCommand("current", new CurrentCommand(devi));
        registerCommand("skip", new SkipCommand(devi));
        registerCommand("shuffle", new ShuffleCommand(devi));
        registerCommand("unshuffle", new UnShuffleCommand(devi));
    }

    private void registerCommand(String commandName, Command command){
        //add
        commands.put(commandName, command);
        unmodifiedCommands.put(commandName, command);
        if(command.getAliases() != null) {
            for (String alias : command.getAliases()) {
                commands.put(alias, command);
            }
        }
    }

    public void handleCommand(String prefix, String raw, MessageReceivedEvent event, CommandSender commandSender){
        CommandParser.CommandContainer container = CommandParser.parseCommand(raw, event);
        Command command = commands.get(raw.toLowerCase().split( " ")[0].substring(prefix.length()));

        //perms check
        if (event != null && event.getGuild() != null && command.getPermission() != null) {
            if (!event.getMember().hasPermission(event.getTextChannel(), command.getPermission()) && !devi.getAdmins().contains(event.getAuthor().getId())) {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(Language.getLanguage(devi.getDeviGuild(event.getGuild().getId()).getSettings().getStringValue(GuildSettings.Settings.LANGUAGE)), 31));
                return;
            }
        }

        //guild only check
        if ((command.guildOnly() && event != null && event.getGuild() == null) || command.guildOnly() && commandSender.isConsoleSender()) {
            commandSender.reply(":warning: **" + devi.getTranslation(Language.ENGLISH, 1) + "**");
            return;
        }

        //all good, run the command
        devi.increaseCommandsExecuted();
        command.execute(container.getArgs(), container.getEvent(), commandSender);
    }

    public ConsoleCommandSender getConsoleCommandSender() {
        return consoleCommandSender;
    }

    public LinkedHashMap<String, Command> getCommands() {
        return commands;
    }

    public LinkedHashMap<String, Command> getUnmodifiedCommands() {
        return unmodifiedCommands;
    }

    public void startConsoleCommandListener() {
        NonblockingBufferedReader reader = new NonblockingBufferedReader(new BufferedReader(new InputStreamReader(System.in)));
        boolean stop = false;
        try {
            while (!stop) {
                String line = reader.readLine();
                if (line != null) {
                    if (line.equals("--stop command-listener")){
                        stop = true;
                    }

                    CommandHandler commandHandler = devi.getCommandHandler();
                    String invoke = line.split(" ")[0].toLowerCase();
                    if (commandHandler.getCommands().containsKey(invoke)) {
                        commandHandler.handleCommand("", line, null, new CommandSender(getConsoleCommandSender(), null));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

