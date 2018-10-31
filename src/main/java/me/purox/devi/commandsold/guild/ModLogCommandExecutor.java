package me.purox.devi.commandsold.guild;

import me.purox.devi.commandsold.guild.handler.*;
import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.List;

public class ModLogCommandExecutor implements CommandExecutor {

    private Devi devi;
    private ModLogEnabledHandler modLogEnabledHandler;
    private ModLogChannelHandler modLogChannelHandler;
    private ModLogMutesHandler modLogMutesHandler;
    private ModLogBansHandler modLogBansHandler;
    private ModLogKicksHandler modLogKicksHandler;
    private ModLogVoiceKicksHandler modLogVoiceKicksHandler;
    private ModLogMessageEditHandler modLogMessageEditHandler;
    private ModLogMessageDeleteHandler modLogMessageDeleteHandler;

    public ModLogCommandExecutor(Devi devi) {
        this.devi = devi;
        this.modLogEnabledHandler = new ModLogEnabledHandler(devi);
        this.modLogChannelHandler = new ModLogChannelHandler(devi);
        this.modLogMutesHandler = new ModLogMutesHandler(devi);
        this.modLogBansHandler = new ModLogBansHandler(devi);
        this.modLogKicksHandler = new ModLogKicksHandler(devi);
        this.modLogVoiceKicksHandler = new ModLogVoiceKicksHandler(devi);
        this.modLogMessageEditHandler = new ModLogMessageEditHandler(devi);
        this.modLogMessageDeleteHandler = new ModLogMessageDeleteHandler(devi);
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("enabled")) {
                modLogEnabledHandler.handle(command, sender);
                return;
            } else if (args[0].equalsIgnoreCase("channel")) {
                modLogChannelHandler.handle(command, sender);
                return;
            } else if (args[0].equalsIgnoreCase("mutes")) {
                modLogMutesHandler.handle(command, sender);
                return;
            } else if (args[0].equalsIgnoreCase("bans")) {
                modLogBansHandler.handle(command, sender);
                return;
            } else if (args[0].equalsIgnoreCase("kicks")) {
                modLogKicksHandler.handle(command, sender);
                return;
            } else if (args[0].equalsIgnoreCase("voicekicks")) {
                modLogVoiceKicksHandler.handle(command, sender);
                return;
            } else if (args[0].equalsIgnoreCase("message-edit") || args[0].equalsIgnoreCase("messageedit")) {
                modLogMessageEditHandler.handle(command, sender);
                return;
            } else if (args[0].equalsIgnoreCase("message-delete") || args[0].equalsIgnoreCase("messagedelete")) {
                modLogMessageDeleteHandler.handle(command, sender);
                return;
            }
        }
        sendModLogEmbed(command, sender);
    }

    private void sendModLogEmbed(ICommand command, CommandSender sender) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.decode("#7289da"));
        embedBuilder.setAuthor(devi.getTranslation(command.getLanguage(), 572));

        for (GuildSettings.Settings setting : GuildSettings.Settings.values()) {
            if (!setting.name().contains("MOD_LOG")) continue;
            Object valObject = command.getDeviGuild().getSettings().getValue(setting);
            String key = setting.getEmoji() + " " + devi.getTranslation(command.getLanguage(), setting.getTranslationID());
            String value = "";

            if (setting.isBooleanValue()){
                value += devi.getTranslation(command.getLanguage(), (Boolean) valObject ? 302 : 303);
            } else if (setting.name().contains("CHANNEL")) {
                TextChannel channel = command.getEvent().getGuild().getTextChannelById(valObject.toString());
                if (channel == null)
                    value += devi.getTranslation(command.getLanguage(), 7, "`unknown`");
                else
                    value += devi.getTranslation(command.getLanguage(), 7, channel.getAsMention());
            } else {
                value += devi.getTranslation(command.getLanguage(), 7, valObject);
            }


            if (setting != GuildSettings.Settings.MOD_LOG_ENABLED)
                value += "\n`" + command.getPrefix() + setting.getCommand() + "`";
             else
                value += "\n`" + command.getPrefix() + "modlog enabled`";
            embedBuilder.addField(key, value, true);
        }
        sender.reply(embedBuilder.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 58;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MANAGEMENT_COMMANDS;
    }
}
