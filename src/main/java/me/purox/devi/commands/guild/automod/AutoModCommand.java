package me.purox.devi.commands.guild.automod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.JavaUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class AutoModCommand implements Command {

    private Devi devi;
    public AutoModCommand(Devi devi) {
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
            embedBuilder.setAuthor(devi.getTranslation(language, 74));
            embedBuilder.setDescription(devi.getTranslation(language, 75, "`" + prefix + "modlog <value> <key>`"));

            //add enabled field
            GuildSettings.Settings autoModEnabled = GuildSettings.Settings.AUTO_MOD_ENABLED;
            embedBuilder.addField(autoModEnabled.getEmoji() + " " + devi.getTranslation(language, autoModEnabled.getTranslationID()), devi.getTranslation(language, 7,"`" + JavaUtils.makeBooleanBeautiful(deviGuild.getSettings().getBooleanValue(autoModEnabled))) + "`" + "\n`" + prefix + "automod enabled <value>`", true);

            //add enabled field
            GuildSettings.Settings autoModAds = GuildSettings.Settings.AUTO_MOD_ANTI_ADS;
            embedBuilder.addField(autoModAds.getEmoji() + " " + devi.getTranslation(language, autoModAds.getTranslationID()), devi.getTranslation(language, 7,"`" + JavaUtils.makeBooleanBeautiful(deviGuild.getSettings().getBooleanValue(autoModAds))) + "`" + "\n`" + prefix + "automod advertisement <value>`", true);

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
        if (args[0].equalsIgnoreCase("enabled")){
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
        } else if (args[0].equalsIgnoreCase("advertisement")){
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 10, "`on`", "`off`"));
                return;
            }
            //update
            deviGuild.getSettings().setBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_ADS, value);
            newValue = String.valueOf(value);
        } else {
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
        return 73;
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
