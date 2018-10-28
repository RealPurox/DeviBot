package me.purox.devi.commandsold.guild;

import me.purox.devi.commandsold.guild.handler.*;
import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.GuildSettings;
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
    public void execute(String[] args, ICommand command, CommandSender sender) {
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

    private void sendAutoModEmbed(ICommand command, CommandSender sender) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.decode("#7289da"));
        embedBuilder.setAuthor(devi.getTranslation(command.getLanguage(), 74));

        for (GuildSettings.Settings setting : GuildSettings.Settings.values()) {
            if (!setting.name().contains("AUTO_MOD")) continue;
            Object valObject = command.getDeviGuild().getSettings().getValue(setting);
            String key = setting.getEmoji() + " " + devi.getTranslation(command.getLanguage(), setting.getTranslationID());
            String value = "";

            if (setting.isBooleanValue()){
                value += devi.getTranslation(command.getLanguage(), (Boolean) valObject ? 302 : 303);
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

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MANAGEMENT_COMMANDS;
    }
}
