package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SettingsCommandExecutor implements CommandExecutor {

    private Devi devi;

    public SettingsCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        //create builder
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(new Color(34, 113, 126));
        embedBuilder.setAuthor(devi.getTranslation(command.getLanguage(), 3, command.getEvent().getGuild().getName()));

        // add fields
        for (GuildSettings.Settings setting : GuildSettings.Settings.values()) {
            if (setting.name().startsWith("MOD_LOG") && setting != GuildSettings.Settings.MOD_LOG_ENABLED ||
                    setting.name().startsWith("AUTO_MOD") && setting != GuildSettings.Settings.AUTO_MOD_ENABLED ||
                    setting.name().startsWith("MUSIC_LOG") && setting != GuildSettings.Settings.MUSIC_LOG_ENABLED ||
                    setting == GuildSettings.Settings.MUTE_ROLE || setting.name().contains("AUTO_ROLE") || setting.name().contains("WELCOME")) {
                continue;
            }

            Object valObject = command.getDeviGuild().getSettings().getValue(setting);
            String key = setting.getEmoji() + " " + devi.getTranslation(command.getLanguage(), setting.getTranslationID());
            String value = "";

            if (setting.name().contains("CHANNEL")) {
                TextChannel channel = command.getEvent().getGuild().getTextChannelById(valObject.toString());
                if (channel == null)
                    value += devi.getTranslation(command.getLanguage(), 7, "`unknown`");
                else
                    value += devi.getTranslation(command.getLanguage(), 7, channel.getAsMention());
            } else if (setting.name().contains("ROLE") && !setting.name().contains("AUTO_ROLE")) {
                System.out.println(valObject.toString());
                Role role = command.getEvent().getGuild().getRoleById(valObject.toString());
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

    @Override
    public ModuleType getModuleType() {
        return ModuleType.INFO_COMMANDS;
    }
}
