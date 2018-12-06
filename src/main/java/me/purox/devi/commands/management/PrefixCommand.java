package me.purox.devi.commands.management;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.entities.Emote;

public class PrefixCommand extends ICommand {

    private Devi devi;

    public PrefixCommand(Devi devi) {
        super("prefix");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length < 1) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "prefix <prefix>`"));
            return;
        }

        String prefix = command.getArgs()[0];
        if (prefix.length() >= 10) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 627));
            return;
        }

        command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.PREFIX, prefix);
        command.getDeviGuild().saveSettings();

        sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 251, "`" + prefix + "`"));
    }
}