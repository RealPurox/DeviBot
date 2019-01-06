package net.devibot.provider.commands.management;

import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.entities.Emote;
import net.devibot.provider.utils.Translator;

public class PrefixCommand extends ICommand {

    private DiscordBot discordBot;

    public PrefixCommand(DiscordBot discordBot) {
        super("prefix");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length < 1) {
            sender.reply(Emote.ERROR + " | " + Translator.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "prefix <prefix>`"));
            return;
        }

        String prefix = command.getArgs()[0];
        if (prefix.length() > 10) {
            sender.reply(Emote.ERROR + " | " + Translator.getTranslation(command.getLanguage(), 627));
            return;
        }

        command.getDeviGuild().setPrefix(prefix);
        discordBot.getMainframeManager().requestDeviGuildSettingsSave(command.getDeviGuild());

        sender.reply(Emote.SUCCESS + " | " + Translator.getTranslation(command.getLanguage(), 251, "`" + prefix + "`"));
    }
}
