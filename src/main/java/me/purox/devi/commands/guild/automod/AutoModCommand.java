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
import net.dv8tion.jda.core.entities.Role;
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
            embedBuilder.setDescription(devi.getTranslation(language, 75, "`" + prefix + "automod <value> <key>`"));

            //add enabled field
            GuildSettings.Settings autoModEnabled = GuildSettings.Settings.AUTO_MOD_ENABLED;
            embedBuilder.addField(autoModEnabled.getEmoji() + " " + devi.getTranslation(language, autoModEnabled.getTranslationID()), devi.getTranslation(language, 7,"`" + JavaUtils.makeBooleanBeautiful(deviGuild.getSettings().getBooleanValue(autoModEnabled))) + "`" + "\n`" + prefix + "automod enabled <value>`", true);

            //add anti advertisement field
            GuildSettings.Settings autoModAds = GuildSettings.Settings.AUTO_MOD_ANTI_ADS;
            embedBuilder.addField(autoModAds.getEmoji() + " " + devi.getTranslation(language, autoModAds.getTranslationID()), devi.getTranslation(language, 7,"`" + JavaUtils.makeBooleanBeautiful(deviGuild.getSettings().getBooleanValue(autoModAds))) + "`" + "\n`" + prefix + "automod advertisement <value>`", true);

            //add bypass roles
            List<String> bypassRoles = deviGuild.getAutoModIgnoredRoles();
            embedBuilder.addField(":white_check_mark: " + devi.getTranslation(language, 79), devi.getTranslation(language, 80)+ ":`" + bypassRoles.size() + "`\n`" + prefix + "automod roles`",true);


            //send builder
            MessageUtils.sendMessage(event.getChannel(), embedBuilder.build());
            return;
        }

        // missed args
        if (args.length < 2 && !args[0].equalsIgnoreCase("roles") && !args[0].equalsIgnoreCase("role")) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "automod <value> <key>`"));
            return;
        }

        String newValue = null; //updated value
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
        } else if (args[0].equalsIgnoreCase("roles") || args[0].equalsIgnoreCase("role")){
            Role role = null;
            if (args.length >= 3 ) role = DiscordUtils.getRole(args[2], event.getGuild());

            // missed args
            if (args.length < 2) {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "automod <list | add | remove>`"));
                return;
            }

            if (args[1].equalsIgnoreCase("list")) {
                if (deviGuild.getAutoModIgnoredRoles().size() == 0) {
                    MessageUtils.sendMessage(event.getChannel(), "There are no roles ignored by auto-mod");
                    return;
                }

                StringBuilder msg = new StringBuilder().append("**The following roles are ignored by auto-mod**:\n\n");
                for (String r : deviGuild.getAutoModIgnoredRoles()) {
                    Role ignoredRole = DiscordUtils.getRole(r, event.getGuild());
                    if (ignoredRole != null)
                        msg.append(ignoredRole.getName() + " (" + ignoredRole.getId() + ")\n");
                }

                MessageUtils.sendMessage(event.getChannel(), msg.toString());
                return;
            }

            else if (args[1].equalsIgnoreCase("add")) {
                // missed args
                if (args.length < 3) {
                    MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "automod role add <role>`"));
                    return;
                }

                if (role == null) {
                    MessageUtils.sendMessage(event.getChannel(), "The role `" + args[2] + "` could not be found");
                    return;
                }

                if (deviGuild.getAutoModIgnoredRoles().contains(role.getId())) {
                    MessageUtils.sendMessage(event.getChannel(), "This role is already being ignored by auto-mod");
                    return;
                }

                deviGuild.getAutoModIgnoredRoles().add(role.getId());
                deviGuild.saveSettings();
                MessageUtils.sendMessage(event.getChannel(), "The role `" + role.getName() + " (" + role.getId() + ")` will now be ignored by auto-mod");
                return;
            }

            else if (args[1].equalsIgnoreCase("remove")) {
                // missed args
                if (args.length < 3) {
                    MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "automod role remove <role>`"));
                    return;
                }

                if (role == null) {
                    MessageUtils.sendMessage(event.getChannel(), "The role `" + args[2] + "` could not be found");
                    return;
                }

                if (!deviGuild.getAutoModIgnoredRoles().contains(role.getId())) {
                    MessageUtils.sendMessage(event.getChannel(), "This role is not being ignored by auto-mod");
                    return;
                }

                deviGuild.getAutoModIgnoredRoles().remove(role.getId());
                deviGuild.saveSettings();
                MessageUtils.sendMessage(event.getChannel(), "The role `" + role.getName() + " (" + role.getId() + ")` will no longer be ignored by auto-mod");
                return;
            } else {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 12, "`" + prefix + "automod <list | add | remove>`"));
                return;
            }
        } else {
            MessageUtils.sendMessage(event.getChannel(), ":warning: " + devi.getTranslation(language, 8, "`" + prefix  + "automod`"));
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
