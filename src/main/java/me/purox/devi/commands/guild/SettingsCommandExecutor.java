package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsCommandExecutor implements CommandExecutor {

    private Devi devi;

    public SettingsCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length == 0) {
            //create builder
            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(new Color(34, 113, 126));
            embedBuilder.setAuthor(devi.getTranslation(command.getLanguage(), 3, command.getEvent().getGuild().getName()));
            embedBuilder.setDescription(devi.getTranslation(command.getLanguage(), 4,"`" + command.getPrefix() + "settings <value> <key>`"));

            // add fields
            for (GuildSettings.Settings setting : GuildSettings.Settings.values()) {
                if (setting.isEditable()) {
                    if (setting == GuildSettings.Settings.MUSIC_LOG_CHANNEL || setting == GuildSettings.Settings.TWITCH_CHANNEL) {
                        TextChannel channel = command.getEvent().getGuild().getTextChannelById(command.getDeviGuild().getSettings().getStringValue(setting == GuildSettings.Settings.MUSIC_LOG_CHANNEL ? GuildSettings.Settings.MUSIC_LOG_CHANNEL : GuildSettings.Settings.TWITCH_CHANNEL));
                        embedBuilder.addField(
                                setting.getEmoji() + " " + devi.getTranslation(command.getLanguage(), setting.getTranslationID()),
                                devi.getTranslation(command.getLanguage(), 7, (channel == null ? "`unknown`" : channel.getAsMention()) + "\n`" + command.getPrefix() + "settings " + setting.name().toLowerCase() + " <value>`"),
                                true);
                        continue;
                    }
                    embedBuilder.addField(
                            setting.getEmoji() + " " + devi.getTranslation(command.getLanguage(), setting.getTranslationID()),
                            devi.getTranslation(command.getLanguage(), 7, "`" + command.getDeviGuild().getSettings().getValue(setting) + "\n" + command.getPrefix() + "settings " + setting.name().toLowerCase() + " <value>`"),
                            true);
                }
            }

            //send message
            sender.reply(embedBuilder.build());
            return;
        }

        // missed arguments
        if (args.length < 2) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, command.getPrefix() + "settings <value> <key>"));
            return;
        }

        Language newLang = Language.getLanguage(args[1]); //new language
        TextChannel newChannel = DiscordUtils.getTextChannel(args[1], command.getEvent().getGuild()); //new text channel
        GuildSettings.Settings settings = GuildSettings.Settings.getSetting(args[0]); // get setting
        // setting not found, send error message
        if (settings == null) {
            sender.reply(":warning: " + devi.getTranslation(command.getLanguage(), 8, "`" + command.getPrefix() + "settings`"));
            return;
        }

        //if the settings is GuildSettings.Settings.LANGUAGE and the new language was not found, send error message
        if(settings == GuildSettings.Settings.LANGUAGE && newLang == null) {
            List<String> langs = new ArrayList<>();
            Arrays.stream(Language.values()).forEach(l -> langs.add(l.name()));

            String message = ":warning: " + devi.getTranslation(command.getLanguage(), 9, langs.stream().collect(Collectors.joining(", ")));
            sender.reply(message);
            return;
        }

        //if the settings is MUSIC_LOG_CHANNEL or TWITCH_CHANNEL and the new channel was not found, send error message
        if ((settings == GuildSettings.Settings.MUSIC_LOG_CHANNEL || settings == GuildSettings.Settings.TWITCH_CHANNEL)&& newChannel == null) {
            sender.reply(devi.getTranslation(command.getLanguage(), 68, "`" + args[1] + "`"));
            return;
        }

        // new value
        String newStringValue = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        if (settings == GuildSettings.Settings.MUSIC_LOG_CHANNEL || settings == GuildSettings.Settings.TWITCH_CHANNEL) {
            newStringValue = newChannel.getId();
            command.getDeviGuild().getSettings().setStringValue(settings, newStringValue);
        } else if (settings.isStringValue()) { //it's a string settings
            //update
            command.getDeviGuild().getSettings().setStringValue(settings, newStringValue);
        } else if (settings.isBooleanValue()) { // it's a boolean setting
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply(devi.getTranslation(command.getLanguage(), 10, "`on`", "`off`"));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setBooleanValue(settings, value);
        } else if (settings.isIntegerValue()) { // it's an int setting
            int i;
            try {
                // parse int
                i = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // not an int, send error message
                sender.reply(devi.getTranslation(command.getLanguage(), 23));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setIntegerValue(settings, i);
        }

        //save settings and send confirmation message in the new language
        command.getDeviGuild().saveSettings();
        Language lang = Language.getLanguage(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        sender.reply(":ok_hand: " + devi.getTranslation(lang, 11, "`" + args[0].toLowerCase() + "`",
                (settings == GuildSettings.Settings.MUSIC_LOG_CHANNEL || settings == GuildSettings.Settings.TWITCH_CHANNEL ? newChannel.getAsMention() : "`" + newStringValue + "`") ));
    }


    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 45;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("setting");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
