package me.purox.devi.commands.handler;

import me.purox.devi.commands.dev.PerformanceCommand;
import me.purox.devi.commands.dev.ReloadCommand;
import me.purox.devi.commands.guild.automod.AutoModCommand;
import me.purox.devi.commands.info.HelpCommand;
import me.purox.devi.commands.mod.*;
import me.purox.devi.commands.guild.SettingsCommand;
import me.purox.devi.commands.guild.modlog.ModLogCommand;
import me.purox.devi.commands.music.*;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.LinkedHashMap;

public class CommandHandler {

    private Devi devi;
    private LinkedHashMap<String, Command> commands = new LinkedHashMap<>();
    private LinkedHashMap<String, Command> unmodifiedCommands = new LinkedHashMap<>();

    public CommandHandler(Devi devi) {
        this.devi = devi;
        //dev commands // only add to 'commands' so they don't appear in !help
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

    public void handleCommand(String prefix, String raw, MessageReceivedEvent event){
        CommandParser.CommandContainer container = CommandParser.parseCommand(raw, event);
        Command command = commands.get(raw.toLowerCase().split( "[ ,\n]")[0].substring(prefix.length()));

        //perms check
        if (event.getGuild() != null && command.getPermission() != null) {
            if (!event.getMember().hasPermission(event.getTextChannel(), command.getPermission()) && !devi.getAdmins().contains(event.getAuthor().getId())) {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(Language.getLanguage(devi.getDeviGuild(event.getGuild().getId()).getSettings().getStringValue(GuildSettings.Settings.LANGUAGE)), 31));
                return;
            }
        }

        //guild only check
        if (command.guildOnly() && event.getGuild() == null) {
            MessageUtils.sendMessage(event.getChannel(), ":warning: **" + devi.getTranslation(Language.ENGLISH, 1) + "**");
            return;
        }

        //all good, run that shit
        command.execute(container.getInvoke(), container.getArgs(), container.getEvent());
    }

    public LinkedHashMap<String, Command> getCommands() {
        return commands;
    }

    public LinkedHashMap<String, Command> getUnmodifiedCommands() {
        return unmodifiedCommands;
    }
}

