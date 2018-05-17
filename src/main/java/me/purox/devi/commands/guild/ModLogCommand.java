package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.JavaUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class ModLogCommand implements Command {

    private Devi devi;
    public ModLogCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        if (args.length == 0) {
            //create builder
            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(new Color(34, 113, 126));
            embedBuilder.setAuthor(devi.getTranslation(language, 61));
            embedBuilder.setDescription(devi.getTranslation(language, 62, "`" + prefix + "modlog <value> <key>`"));

            //add channel field
            GuildSettings.Settings modLogChannel = GuildSettings.Settings.MOD_LOG_CHANNEL;
            TextChannel channel = event.getGuild().getTextChannelById(deviGuild.getSettings().getStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL));
            embedBuilder.addField(modLogChannel.getEmoji() + " " + devi.getTranslation(language, modLogChannel.getTranslationID()),devi.getTranslation(language, 7, channel == null ? "`undefined`" : channel.getAsMention()) + "\n`" + prefix + "modlog channel <value>`", true);

            //add enabled field
            GuildSettings.Settings modLogEnabled = GuildSettings.Settings.MOD_LOG_ENABLED;
            embedBuilder.addField(modLogEnabled.getEmoji() + " " + devi.getTranslation(language, modLogEnabled.getTranslationID()), devi.getTranslation(language, 7,"`" + JavaUtils.makeBooleanBeautiful(deviGuild.getSettings().getBooleanValue(modLogEnabled))) + "`" + "\n`" + prefix + "modlog enabled <value>`", true);

            //add bans field
            GuildSettings.Settings modLogBans = GuildSettings.Settings.MOD_LOG_BANS;
            embedBuilder.addField(modLogBans.getEmoji() + " " + devi.getTranslation(language, modLogBans.getTranslationID()), devi.getTranslation(language, 7,"`" + JavaUtils.makeBooleanBeautiful(deviGuild.getSettings().getBooleanValue(modLogBans))) + "`" + "\n`" + prefix + "modlog bans <value>`", true);

            //add mutes field
            GuildSettings.Settings modLogMutes = GuildSettings.Settings.MOD_LOG_MUTES;
            embedBuilder.addField(modLogMutes.getEmoji() + " " + devi.getTranslation(language, modLogMutes.getTranslationID()), devi.getTranslation(language, 7,"`" + JavaUtils.makeBooleanBeautiful(deviGuild.getSettings().getBooleanValue(modLogMutes))) + "`" + "\n`" + prefix + "modlog mutes <value>`", true);

            //send builder
            MessageUtils.sendMessage(event.getChannel(), embedBuilder.build());
            return;
        }

        // missed args
        if (args.length < 2) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "modlog <value> <key>`"));
            return;
        }

        String newValue; //updated value
        if (args[0].equalsIgnoreCase("channel")) {
            //get channel
            TextChannel newChannel = DiscordUtils.getTextChannel(args[1], event.getGuild());
            //channel not found, send error message
            if (newChannel == null) {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 68, args[1]));
                return;
            }
            //update
            deviGuild.getSettings().setStringValue(GuildSettings.Settings.MOD_LOG_CHANNEL, newChannel.getId());
            newValue = newChannel.getName();
        } else if (args[0].equalsIgnoreCase("enabled")){
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 10, "`on`", "`off`"));
                return;
            }
            //update
            deviGuild.getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_ENABLED, value);
            newValue = String.valueOf(value);
        } else if (args[0].equalsIgnoreCase("mutes")) {
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 10, "`on`", "`off`"));
                return;
            }
            //update
            deviGuild.getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_MUTES, value);
            newValue = String.valueOf(value);
        } else if (args[0].equalsIgnoreCase("bans")) {
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 10, "`on`", "`off`"));
                return;
            }
            //update
            deviGuild.getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_BANS, value);
            newValue = String.valueOf(value);
        }
        else {
            MessageUtils.sendMessage(event.getChannel(), ":warning: " + devi.getTranslation(language, 8, "`" + prefix  + "modlog`"));
            return;
        }

        //save settings and send message
        deviGuild.saveSettings();
        MessageUtils.sendMessage(event.getChannel(), ":ok_hand: " + devi.getTranslation(language, 11, "`" + args[0].toLowerCase() + "`", "`" + newValue + "`"));
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
