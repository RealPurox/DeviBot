package net.devibot.provider.commands.management;

import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.core.entities.Emote;
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
            sender.errorMessage().appendTranslation("commands.general.error.arguments", "`" + command.getDeviGuild().getPrefix() + "prefix <prefix>`").execute();
            return;
        }

        String prefix = command.getArgs()[0];
        if (prefix.length() > 10) {
            sender.errorMessage().appendTranslation("commands.prefix.error.limit").execute();
            return;
        }

        command.getDeviGuild().setPrefix(prefix);
        discordBot.getMainframeManager().saveDeviGuild(command.getDeviGuild());

        sender.errorMessage().appendTranslation("commands.prefix.success.changed", "`" + prefix + "`").execute();
    }
}
