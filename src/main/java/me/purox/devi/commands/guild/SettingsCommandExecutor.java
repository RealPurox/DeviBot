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
import net.dv8tion.jda.core.entities.Role;
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
        //create builder
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(new Color(34, 113, 126));
        embedBuilder.setAuthor(devi.getTranslation(command.getLanguage(), 3, command.getEvent().getGuild().getName()));

        // add fields
        for (GuildSettings.Settings setting : GuildSettings.Settings.values()) {
            if (setting.name().startsWith("MOD_LOG") && setting != GuildSettings.Settings.MOD_LOG_ENABLED ||
                    setting.name().startsWith("AUTO_MOD") && setting != GuildSettings.Settings.AUTO_MOD_ENABLED ||
                    setting.name().startsWith("MUSIC_LOG") && setting != GuildSettings.Settings.MUSIC_LOG_ENABLED) {
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
            } else if (setting.name().contains("ROLE")) {
                Role role = command.getEvent().getGuild().getRoleById(valObject.toString());
                if (role == null)
                    value += devi.getTranslation(command.getLanguage(), 7, "`unknown`");
                else
                    value += devi.getTranslation(command.getLanguage(), 7, role.getAsMention());
            } else if (setting.isBooleanValue()){
                value += devi.getTranslation(command.getLanguage(), 7, JavaUtils.makeBooleanBeautiful(command.getDeviGuild().getSettings().getBooleanValue(setting)));
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
}
