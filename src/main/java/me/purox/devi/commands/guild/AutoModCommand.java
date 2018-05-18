package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.JavaUtils;
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
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
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
            embedBuilder.addField(autoModEnabled.getEmoji() + " " + devi.getTranslation(language, autoModEnabled.getTranslationID()),
                    devi.getTranslation(language, 7,"`" + JavaUtils.makeBooleanBeautiful(deviGuild.getSettings().getBooleanValue(autoModEnabled))) + "`"
                            + "\n`" + prefix + "automod enabled <value>`", false);

            //add anti advertisement field
            GuildSettings.Settings autoModAds = GuildSettings.Settings.AUTO_MOD_ANTI_ADS;
            embedBuilder.addField(autoModAds.getEmoji() + " " + devi.getTranslation(language, autoModAds.getTranslationID()),
                    devi.getTranslation(language, 7,"`" + JavaUtils.makeBooleanBeautiful(deviGuild.getSettings().getBooleanValue(autoModAds))) + "`"
                            + "\n`" + prefix + "automod advertisement <value>`", false);

            //add anti caps field field
            GuildSettings.Settings autoModAntiCaps = GuildSettings.Settings.AUTO_MOD_ANTI_CAPS;
            embedBuilder.addField(autoModAntiCaps.getEmoji() + " " + devi.getTranslation(language, autoModAntiCaps.getTranslationID()),
                    devi.getTranslation(language, 7,"`" + JavaUtils.makeBooleanBeautiful(deviGuild.getSettings().getBooleanValue(autoModAntiCaps))) + "`"
                            + "\n`" + prefix + "automod caps <value>`", false);

            //add anti emoji spam field field
            GuildSettings.Settings autoModAntiEmoji = GuildSettings.Settings.AUTO_MOD_ANTI_EMOJI;
            embedBuilder.addField(autoModAntiEmoji.getEmoji() + " " + devi.getTranslation(language, autoModAntiEmoji.getTranslationID()),
                    devi.getTranslation(language, 7,"`" + JavaUtils.makeBooleanBeautiful(deviGuild.getSettings().getBooleanValue(autoModAntiEmoji))) + "`"
                            + "\n`" + prefix + "automod emoji <value>`", false);


            //add bypass roles
            List<String> bypassRoles = deviGuild.getAutoModIgnoredRoles();
            embedBuilder.addField(":white_check_mark: " + devi.getTranslation(language, 79), "`" + prefix + "automod roles`",true);

            //send builder
            sender.reply( embedBuilder.build());
            return;
        }

        // missed args
        if (args.length < 2 && !args[0].equalsIgnoreCase("roles") && !args[0].equalsIgnoreCase("role")) {
            sender.reply( devi.getTranslation(language, 12, "`" + prefix + "automod <value> <key>`"));
            return;
        }

        String newValue; //updated value
        if (args[0].equalsIgnoreCase("enabled")){
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply( devi.getTranslation(language, 10, "`on`", "`off`"));
                return;
            }
            //update
            deviGuild.getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_BANS, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("advertisement")){
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply( devi.getTranslation(language, 10, "`on`", "`off`"));
                return;
            }
            //update
            deviGuild.getSettings().setBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_ADS, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("caps")) {
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply(devi.getTranslation(language, 10, "`on`", "`off`"));
                return;
            }
            //update
            deviGuild.getSettings().setBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_CAPS, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("emoji")) {
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply(devi.getTranslation(language, 10, "`on`", "`off`"));
                return;
            }
            //update
            deviGuild.getSettings().setBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_EMOJI, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("roles") || args[0].equalsIgnoreCase("role")){
            Role role = null;
            if (args.length >= 3 ) role = DiscordUtils.getRole(args[2], event.getGuild());

            // missed args
            if (args.length < 2) {
                sender.reply( devi.getTranslation(language, 12, "`" + prefix + "automod roles <list | add | remove>`"));
                return;
            }

            if (args[1].equalsIgnoreCase("list")) {
                if (deviGuild.getAutoModIgnoredRoles().size() == 0) {
                    sender.reply(devi.getTranslation(language, 170));
                    return;
                }

                StringBuilder msg = new StringBuilder().append("**").append(devi.getTranslation(language, 171)).append("**:\n\n");
                for (String r : deviGuild.getAutoModIgnoredRoles()) {
                    Role ignoredRole = DiscordUtils.getRole(r, event.getGuild());
                    if (ignoredRole != null)
                        msg.append(ignoredRole.getName()).append(" ( ").append(ignoredRole.getId()).append(" )\n");
                }

                sender.reply( msg.toString());
                return;
            }

            else if (args[1].equalsIgnoreCase("add")) {
                // missed args
                if (args.length < 3) {
                    sender.reply( devi.getTranslation(language, 12, "`" + prefix + "automod role add <role>`"));
                    return;
                }

                if (role == null) {
                    sender.reply(devi.getTranslation(language, 172,  "`" + args[2] + "`"));
                    return;
                }

                if (deviGuild.getAutoModIgnoredRoles().contains(role.getId())) {
                    sender.reply(devi.getTranslation(language, 173));
                    return;
                }

                deviGuild.getAutoModIgnoredRoles().add(role.getId());
                deviGuild.saveSettings();
                sender.reply( devi.getTranslation(language, 174, role.getName() + " ( " + role.getId() + " )"));
                return;
            }

            else if (args[1].equalsIgnoreCase("remove")) {
                // missed args
                if (args.length < 3) {
                    sender.reply( devi.getTranslation(language, 12, "`" + prefix + "automod role remove <role>`"));
                    return;
                }

                if (role == null) {
                    sender.reply(devi.getTranslation(language, 172,  "`" + args[2] + "`"));
                    return;
                }

                if (!deviGuild.getAutoModIgnoredRoles().contains(role.getId())) {
                    sender.reply(devi.getTranslation(language, 175));
                    return;
                }

                deviGuild.getAutoModIgnoredRoles().remove(role.getId());
                deviGuild.saveSettings();
                sender.reply(devi.getTranslation(language, 176, role.getName() + " ( " + role.getId() + " )"));
                return;
            } else {
                sender.reply( devi.getTranslation(language, 12, "`" + prefix + "automod <list | add | remove>`"));
                return;
            }
        } else {
            sender.reply( ":warning: " + devi.getTranslation(language, 8, "`" + prefix  + "automod`"));
            return;
        }
        //save settings and send message
        deviGuild.saveSettings();
        sender.reply( ":ok_hand: " + devi.getTranslation(language, 11, "`" + args[0].toLowerCase() + "`", "`" + newValue + "`"));
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
