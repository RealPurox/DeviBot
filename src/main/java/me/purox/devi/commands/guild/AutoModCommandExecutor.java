package me.purox.devi.commands.guild;

import me.purox.devi.commands.guild.handler.*;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.List;

public class AutoModCommandExecutor implements CommandExecutor {

    private Devi devi;
    private AutoModEnabledHandler autoModEnabledHandler;
    private AutoModAdsHandler autoModAdsHandler;
    private AutoModCapsHandler autoModCapsHandler;
    private AutoModEmojiHandler autoModEmojiHandler;
    private AutoModRolesHandler autoModRolesHandler;

    public AutoModCommandExecutor(Devi devi) {
        this.devi = devi;
        this.autoModEnabledHandler = new AutoModEnabledHandler(devi);
        this.autoModAdsHandler = new AutoModAdsHandler(devi);
        this.autoModCapsHandler = new AutoModCapsHandler(devi);
        this.autoModEmojiHandler = new AutoModEmojiHandler(devi);
        this.autoModRolesHandler = new AutoModRolesHandler(devi);
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("enabled")) {
                autoModEnabledHandler.handle(command, sender);
                return;
            } else if (args[0].equalsIgnoreCase("ads")) {
                autoModAdsHandler.handle(command, sender);
                return;
            } else if (args[0].equalsIgnoreCase("caps")) {
                autoModCapsHandler.handle(command, sender);
                return;
            } else if (args[0].equalsIgnoreCase("emoji")) {
                autoModEmojiHandler.handle(command, sender);
                return;
            } else if (args[0].equalsIgnoreCase("roles")) {
                autoModRolesHandler.handle(command, sender);
                return;
            }
        }
        sendAutoModEmbed(command, sender);
    }

    private void sendAutoModEmbed(Command command, CommandSender sender) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(new Color(34, 113, 126));
        embedBuilder.setAuthor(devi.getTranslation(command.getLanguage(), 74));

        for (GuildSettings.Settings setting : GuildSettings.Settings.values()) {
            if (!setting.name().contains("AUTO_MOD")) continue;
            Object valObject = command.getDeviGuild().getSettings().getValue(setting);
            String key = setting.getEmoji() + " " + devi.getTranslation(command.getLanguage(), setting.getTranslationID());
            String value = "";

            if (setting.isBooleanValue()){
                value += devi.getTranslation(command.getLanguage(), 7, JavaUtils.makeBooleanBeautiful(command.getDeviGuild().getSettings().getBooleanValue(setting)));
            } else {
                value += devi.getTranslation(command.getLanguage(), 7, valObject);
            }


            if (setting != GuildSettings.Settings.AUTO_MOD_ENABLED) {
                value += "\n`" + command.getPrefix() + setting.getCommand() + "`";
            } else {
                value += "\n`" + command.getPrefix() + "automod enabled`";
            }
            embedBuilder.addField(key, value, true);
        }
        embedBuilder.addField(":no_bell: " + devi.getTranslation(command.getLanguage(), 79), "`" + command.getPrefix() + "automod roles`",true);
        sender.reply(embedBuilder.build());
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
