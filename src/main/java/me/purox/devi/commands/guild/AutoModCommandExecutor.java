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
import net.dv8tion.jda.core.entities.Role;

import java.awt.*;
import java.util.List;

public class AutoModCommandExecutor implements CommandExecutor {

    private Devi devi;
    public AutoModCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length == 0) {
            //create builder
            EmbedBuilder embedBuilder = new EmbedBuilder().setColor(new Color(34, 113, 126));
            embedBuilder.setAuthor(devi.getTranslation(command.getLanguage(), 74));
            embedBuilder.setDescription(devi.getTranslation(command.getLanguage(), 75, "`" + command.getPrefix() + "automod <value> <key>`"));

            //add enabled field
            GuildSettings.Settings autoModEnabled = GuildSettings.Settings.AUTO_MOD_ENABLED;
            embedBuilder.addField(autoModEnabled.getEmoji() + " " + devi.getTranslation(command.getLanguage(), autoModEnabled.getTranslationID()),
                    devi.getTranslation(command.getLanguage(), 7,"`" + JavaUtils.makeBooleanBeautiful(command.getDeviGuild().getSettings().getBooleanValue(autoModEnabled))) + "`"
                            + "\n`" + command.getPrefix() + "automod enabled <value>`", false);

            //add anti advertisement field
            GuildSettings.Settings autoModAds = GuildSettings.Settings.AUTO_MOD_ANTI_ADS;
            embedBuilder.addField(autoModAds.getEmoji() + " " + devi.getTranslation(command.getLanguage(), autoModAds.getTranslationID()),
                    devi.getTranslation(command.getLanguage(), 7,"`" + JavaUtils.makeBooleanBeautiful(command.getDeviGuild().getSettings().getBooleanValue(autoModAds))) + "`"
                            + "\n`" + command.getPrefix() + "automod advertisement <value>`", false);

            //add anti caps field field
            GuildSettings.Settings autoModAntiCaps = GuildSettings.Settings.AUTO_MOD_ANTI_CAPS;
            embedBuilder.addField(autoModAntiCaps.getEmoji() + " " + devi.getTranslation(command.getLanguage(), autoModAntiCaps.getTranslationID()),
                    devi.getTranslation(command.getLanguage(), 7,"`" + JavaUtils.makeBooleanBeautiful(command.getDeviGuild().getSettings().getBooleanValue(autoModAntiCaps))) + "`"
                            + "\n`" + command.getPrefix() + "automod caps <value>`", false);

            //add anti emoji spam field field
            GuildSettings.Settings autoModAntiEmoji = GuildSettings.Settings.AUTO_MOD_ANTI_EMOJI;
            embedBuilder.addField(autoModAntiEmoji.getEmoji() + " " + devi.getTranslation(command.getLanguage(), autoModAntiEmoji.getTranslationID()),
                    devi.getTranslation(command.getLanguage(), 7,"`" + JavaUtils.makeBooleanBeautiful(command.getDeviGuild().getSettings().getBooleanValue(autoModAntiEmoji))) + "`"
                            + "\n`" + command.getPrefix() + "automod emoji <value>`", false);


            //add bypass roles
            List<String> bypassRoles = command.getDeviGuild().getAutoModIgnoredRoles();
            embedBuilder.addField(":white_check_mark: " + devi.getTranslation(command.getLanguage(), 79), "`" + command.getPrefix() + "automod roles`",true);

            //send builder
            sender.reply( embedBuilder.build());
            return;
        }

        // missed args
        if (args.length < 2 && !args[0].equalsIgnoreCase("roles") && !args[0].equalsIgnoreCase("role")) {
            sender.reply( devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "automod <value> <key>`"));
            return;
        }

        String newValue; //updated value
        if (args[0].equalsIgnoreCase("enabled")){
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply( devi.getTranslation(command.getLanguage(), 10, "`on`", "`off`"));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.MOD_LOG_BANS, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("advertisement")){
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply( devi.getTranslation(command.getLanguage(), 10, "`on`", "`off`"));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_ADS, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("caps")) {
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply(devi.getTranslation(command.getLanguage(), 10, "`on`", "`off`"));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_CAPS, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("emoji")) {
            //get boolean
            Boolean value = JavaUtils.getBoolean(args[1]);
            //boolean not found, send error message
            if (value == null) {
                sender.reply(devi.getTranslation(command.getLanguage(), 10, "`on`", "`off`"));
                return;
            }
            //update
            command.getDeviGuild().getSettings().setBooleanValue(GuildSettings.Settings.AUTO_MOD_ANTI_EMOJI, value);
            newValue = JavaUtils.makeBooleanBeautiful(value);
        } else if (args[0].equalsIgnoreCase("roles") || args[0].equalsIgnoreCase("role")){
            Role role = null;
            if (args.length >= 3 ) role = DiscordUtils.getRole(args[2], command.getEvent().getGuild());

            // missed args
            if (args.length < 2) {
                sender.reply( devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "automod roles <list | add | remove>`"));
                return;
            }

            if (args[1].equalsIgnoreCase("list")) {
                if (command.getDeviGuild().getAutoModIgnoredRoles().size() == 0) {
                    sender.reply(devi.getTranslation(command.getLanguage(), 170));
                    return;
                }

                StringBuilder msg = new StringBuilder().append("**").append(devi.getTranslation(command.getLanguage(), 171)).append("**:\n\n");
                for (String r : command.getDeviGuild().getAutoModIgnoredRoles()) {
                    Role ignoredRole = DiscordUtils.getRole(r, command.getEvent().getGuild());
                    if (ignoredRole != null)
                        msg.append(ignoredRole.getName()).append(" ( ").append(ignoredRole.getId()).append(" )\n");
                }

                sender.reply( msg.toString());
                return;
            }

            else if (args[1].equalsIgnoreCase("add")) {
                // missed args
                if (args.length < 3) {
                    sender.reply( devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "automod role add <role>`"));
                    return;
                }

                if (role == null) {
                    sender.reply(devi.getTranslation(command.getLanguage(), 172,  "`" + args[2] + "`"));
                    return;
                }

                if (command.getDeviGuild().getAutoModIgnoredRoles().contains(role.getId())) {
                    sender.reply(devi.getTranslation(command.getLanguage(), 173));
                    return;
                }

                command.getDeviGuild().getAutoModIgnoredRoles().add(role.getId());
                command.getDeviGuild().saveSettings();
                sender.reply( devi.getTranslation(command.getLanguage(), 174, role.getName() + " ( " + role.getId() + " )"));
                return;
            }

            else if (args[1].equalsIgnoreCase("remove")) {
                // missed args
                if (args.length < 3) {
                    sender.reply( devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "automod role remove <role>`"));
                    return;
                }

                if (role == null) {
                    sender.reply(devi.getTranslation(command.getLanguage(), 172,  "`" + args[2] + "`"));
                    return;
                }

                if (!command.getDeviGuild().getAutoModIgnoredRoles().contains(role.getId())) {
                    sender.reply(devi.getTranslation(command.getLanguage(), 175));
                    return;
                }

                command.getDeviGuild().getAutoModIgnoredRoles().remove(role.getId());
                command.getDeviGuild().saveSettings();
                sender.reply(devi.getTranslation(command.getLanguage(), 176, role.getName() + " ( " + role.getId() + " )"));
                return;
            } else {
                sender.reply( devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "automod <list | add | remove>`"));
                return;
            }
        } else {
            sender.reply( ":warning: " + devi.getTranslation(command.getLanguage(), 8, "`" + command.getPrefix()  + "automod`"));
            return;
        }
        //save settings and send message
        command.getDeviGuild().saveSettings();
        sender.reply( ":ok_hand: " + devi.getTranslation(command.getLanguage(), 11, "`" + args[0].toLowerCase() + "`", "`" + newValue + "`"));
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
