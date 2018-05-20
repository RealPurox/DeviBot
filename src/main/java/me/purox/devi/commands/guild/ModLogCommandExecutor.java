package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.List;

public class ModLogCommandExecutor implements CommandExecutor {

    private Devi devi;
    public ModLogCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length == 0) {
            //create builder
            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(new Color(34, 113, 126));
            embedBuilder.setAuthor(devi.getTranslation(command.getLanguage(), 61));
            embedBuilder.setDescription(devi.getTranslation(command.getLanguage(), 62, "`" + command.getPrefix() + "modlog <value> <key>`"));

            //add enabled field
            GuildSettings.Settings modLogEnabled = GuildSettings.Settings.MOD_LOG_ENABLED;
            embedBuilder.addField(modLogEnabled.getEmoji() + " " + devi.getTranslation(command.getLanguage(), modLogEnabled.getTranslationID()), devi.getTranslation(command.getLanguage(), 7,"`" + JavaUtils.makeBooleanBeautiful(command.getDeviGuild().getSettings().getBooleanValue(modLogEnabled))) + "`" + "\n`" + command.getPrefix() + "modlog enabled <value>`", true);

            //add channel field
            GuildSettings.Settings modLogChannel = GuildSettings.Settings.MOD_LOG_CHANNEL;
            TextChannel channel = command.getEvent().getGuild().getTextChannelById(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL));
            embedBuilder.addField(modLogChannel.getEmoji() + " " + devi.getTranslation(command.getLanguage(), modLogChannel.getTranslationID()),devi.getTranslation(command.getLanguage(), 7, channel == null ? "`undefined`" : channel.getAsMention()) + "\n`" + command.getPrefix() + "modlog channel <value>`", true);

            //add bans field
            GuildSettings.Settings modLogBans = GuildSettings.Settings.MOD_LOG_BANS;
            embedBuilder.addField(modLogBans.getEmoji() + " " + devi.getTranslation(command.getLanguage(), modLogBans.getTranslationID()), devi.getTranslation(command.getLanguage(), 7,"`" + JavaUtils.makeBooleanBeautiful(command.getDeviGuild().getSettings().getBooleanValue(modLogBans))) + "`" + "\n`" + command.getPrefix() + "modlog bans <value>`", true);

            //add mutes field
            GuildSettings.Settings modLogMutes = GuildSettings.Settings.MOD_LOG_MUTES;
            embedBuilder.addField(modLogMutes.getEmoji() + " " + devi.getTranslation(command.getLanguage(), modLogMutes.getTranslationID()), devi.getTranslation(command.getLanguage(), 7,"`" + JavaUtils.makeBooleanBeautiful(command.getDeviGuild().getSettings().getBooleanValue(modLogMutes))) + "`" + "\n`" + command.getPrefix() + "modlog mutes <value>`", true);

            //add edited messages field
            GuildSettings.Settings modLogEditedMessages = GuildSettings.Settings.MOD_LOG_MESSAGE_EDITED;
            embedBuilder.addField(modLogEditedMessages.getEmoji() + " " + devi.getTranslation(command.getLanguage(), modLogEditedMessages.getTranslationID()), devi.getTranslation(command.getLanguage(), 7, "`" + JavaUtils.makeBooleanBeautiful(command.getDeviGuild().getSettings().getBooleanValue(modLogEditedMessages)) + "`" + "\n`" + command.getPrefix() + "modlog message_edit <value>`"), true);

            //add delete messages field
            GuildSettings.Settings modLogDeletedMessages = GuildSettings.Settings.MOD_LOG_MESSAGE_DELETED;
            embedBuilder.addField(modLogDeletedMessages.getEmoji() + " " + devi.getTranslation(command.getLanguage(), modLogDeletedMessages.getTranslationID()), devi.getTranslation(command.getLanguage(), 7, "`" + JavaUtils.makeBooleanBeautiful(command.getDeviGuild().getSettings().getBooleanValue(modLogDeletedMessages)) + "`" + "\n`" + command.getPrefix() + "modlog message_delete <value>`"), true);

            //send builder
            sender.reply(embedBuilder.build());
            return;
        }

        // missed args
        if (args.length < 2) {
            sender.reply( devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "modlog <value> <key>`"));
            return;
        }

        String newValue; //updated value
        if (args[0].equalsIgnoreCase("channel")) {
            //get channel
            TextChannel newChannel = DiscordUtils.getTextChannel(args[1], command.getEvent().getGuild());
            //channel not found, send error message
            if (newChannel == null) {
                sender.reply(devi.getTranslation(command.getLanguage(), 68, args[1]));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL, newChannel.getId());
            newValue = newChannel.getName();
        } else if (args[0].equalsIgnoreCase("enabled")){
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply( devi.getTranslation(command.getLanguage(), 10, "`on`", "`off`"));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("bans")) {
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply(devi.getTranslation(command.getLanguage(), 10, "`on`", "`off`"));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_BANS, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("mutes")) {
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply( devi.getTranslation(command.getLanguage(), 10, "`on`", "`off`"));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_MUTES, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("message_edit")) {
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply( devi.getTranslation(command.getLanguage(), 10, "`on`", "`off`"));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_MESSAGE_EDITED, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("message_delete")) {
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply( devi.getTranslation(command.getLanguage(), 10, "`on`", "`off`"));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_MESSAGE_DELETED, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else {
            sender.reply(":warning: " + devi.getTranslation(command.getLanguage(), 8, "`" + command.getPrefix()  + "modlog`"));
            return;
        }

        //save settings and send message
        command.getDeviGuild().saveSettings();
        sender.reply(":ok_hand: " + devi.getTranslation(command.getLanguage(), 11, "`" + args[0].toLowerCase() + "`", "`" + newValue + "`"));
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
}
