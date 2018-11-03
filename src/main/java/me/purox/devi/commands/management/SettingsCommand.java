package me.purox.devi.commands.management;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;

public class SettingsCommand extends ICommand {

    private Devi devi;

    public SettingsCommand(Devi devi) {
        super("settings", "setting");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        //create builder
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.decode("#7289da"));
        embedBuilder.setAuthor(devi.getTranslation(command.getLanguage(), 3, command.getGuild().getName()));

        // add fields
        for (GuildSettings.Settings setting : GuildSettings.Settings.values()) {
            if (setting == GuildSettings.Settings.MUSIC_LOG_ENABLED) continue;
            if (!setting.isDisplayedInSettings()) continue;


            Object valObject = command.getDeviGuild().getSettings().getValue(setting);
            String key = setting.getEmoji() + " " + devi.getTranslation(command.getLanguage(), setting.getTranslationID());
            String value = "";

            if (setting.name().contains("CHANNEL")) {
                TextChannel channel = command.getGuild().getTextChannelById(valObject.toString());
                if (channel == null)
                    value += devi.getTranslation(command.getLanguage(), 7, "`unknown`");
                else
                    value += devi.getTranslation(command.getLanguage(), 7, channel.getAsMention());
            } else if (setting.name().contains("ROLE") && !setting.name().contains("AUTO_ROLE")) {
                Role role = command.getGuild().getRoleById(valObject.toString());
                if (role == null)
                    value += devi.getTranslation(command.getLanguage(), 7, "`unknown`");
                else
                    value += devi.getTranslation(command.getLanguage(), 7, role.getAsMention());
            } else if (setting.isBooleanValue()){
                value += devi.getTranslation(command.getLanguage(), (Boolean) valObject ? 302 : 303);
            } else  if (setting == GuildSettings.Settings.LANGUAGE) {
                Language language = Language.getLanguage((String) valObject);
                //will never be null but intellij ain't happy without this check
                if (language == null) return;
                value += devi.getTranslation(command.getLanguage(), 7, language.getName());
            } else {
                value += devi.getTranslation(command.getLanguage(), 7, valObject);
            }

            value += "\n`" + command.getPrefix() + setting.getCommand() + "`";
            embedBuilder.addField(key, value, true);
        }

        //send message
        sender.reply(embedBuilder.build());

    }
}
