package me.purox.devi.commands.management;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.management.handler.*;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public class AutoModCommand extends ICommand {

    private Devi devi;

    private AutoModEnabledHandler autoModEnabledHandler;
    private AutoModAdsHandler autoModAdsHandler;
    private AutoModCapsHandler autoModCapsHandler;
    private AutoModEmojiHandler autoModEmojiHandler;
    private AutoModRolesHandler autoModRolesHandler;


    public AutoModCommand(Devi devi) {
        super("automod");
        this.devi = devi;

        this.autoModEnabledHandler = new AutoModEnabledHandler(devi);
        this.autoModAdsHandler = new AutoModAdsHandler(devi);
        this.autoModCapsHandler = new AutoModCapsHandler(devi);
        this.autoModEmojiHandler = new AutoModEmojiHandler(devi);
        this.autoModRolesHandler = new AutoModRolesHandler(devi);
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String[] args = command.getArgs();

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("enabled")) {
                autoModEnabledHandler.handle(command);
                return;
            } else if (args[0].equalsIgnoreCase("ads")) {
                autoModAdsHandler.handle(command);
                return;
            } else if (args[0].equalsIgnoreCase("caps")) {
                autoModCapsHandler.handle(command);
                return;
            } else if (args[0].equalsIgnoreCase("emoji")) {
                autoModEmojiHandler.handle(command);
                return;
            } else if (args[0].equalsIgnoreCase("roles")) {
                autoModRolesHandler.handle(sender, command);
                return;
            }
        }
        sendAutoModEmbed(command, sender);

    }

    private void sendAutoModEmbed(ICommand.Command command, CommandSender sender) {
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

}
