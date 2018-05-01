package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.JavaUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsCommand implements Command {

    private Devi devi;

    public SettingsCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String command, String[] args, MessageReceivedEvent event) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        if (args.length == 0) {
            //create builder
            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(new Color(34, 113, 126));
            embedBuilder.setAuthor(devi.getTranslation(language, 3, event.getGuild().getName()));
            embedBuilder.setDescription(devi.getTranslation(language, 4,"`" + prefix + "settings <value> <key>`"));

            // add fields
            for (GuildSettings.Settings setting : GuildSettings.Settings.values()) {
                if (setting.isEditable()) {
                    embedBuilder.addField(
                            setting.getEmoji() + " " + devi.getTranslation(language, setting.getTranslationID()),
                            devi.getTranslation(language, 7, "`" + deviGuild.getSettings().getValue(setting) + "\n" + prefix + "settings " + setting.name().toLowerCase() + " <value>`"),
                            true);
                }
            }

            //send message
            MessageUtils.sendMessage(event.getChannel(), embedBuilder.build());
            return;
        }

        // missed arguments
        if (args.length < 2) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, prefix + "settings <value> <key>"));
            return;
        }

        Language newLang = Language.getLanguage(args[1]); //new language
        GuildSettings.Settings settings = GuildSettings.Settings.getSetting(args[0]); // get setting
        // setting not found, send error message
        if (settings == null) {
            MessageUtils.sendMessage(event.getChannel(), ":warning: " + devi.getTranslation(language, 8, "`" + prefix + "settings`"));
            return;
        }

        //if the settings is {GuildSettings.Settings.LANGUAGE} and the new language was not found, send error message
        if(settings == GuildSettings.Settings.LANGUAGE && newLang == null) {
            List<String> langs = new ArrayList<>();
            Arrays.stream(Language.values()).forEach(l -> langs.add(l.name()));

            String message = ":warning: " + devi.getTranslation(language, 9, langs.stream().collect(Collectors.joining(", ")));
            MessageUtils.sendMessage(event.getChannel(), message);
            return;
        }

        // new value
        String newStringValue = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
        if (settings.isStringValue()) { //it's a string settings
            //update
            deviGuild.getSettings().setStringValue(settings, newStringValue);
        } else if (settings.isBooleanValue()) { // it's a boolean setting
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 10, "`on`", "`off`"));
                return;
            }
            //update
            deviGuild.getSettings().setBooleanValue(settings, value);
        } else if (settings.isIntegerValue()) { // it's an int setting
            int i;
            try {
                // parse int
                i = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                // not an int, send error message
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 23));
                return;
            }
            //update
            deviGuild.getSettings().setIntegerValue(settings, i);
        }

        //save settings and send confirmation message in the new language
        deviGuild.saveSettings();
        Language lang = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        MessageUtils.sendMessage(event.getChannel(), ":ok_hand: " + devi.getTranslation(lang, 11, "`" + args[0].toLowerCase() + "`", "`" + newStringValue + "`"));
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
