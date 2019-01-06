package net.devibot.provider.commands.dev;

import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;

public class TestCommand extends ICommand {

    private DiscordBot discordBot;

    public TestCommand(DiscordBot discordBot) {
        super("test", "testing", "tester");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        sender.reply("Command Handler works!!!");
    }
}
