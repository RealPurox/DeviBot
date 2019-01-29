package net.devibot.provider.commands.dev;

import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCommand extends ICommand {


    private final Logger logger = LoggerFactory.getLogger(TestCommand.class);

    private DiscordBot discordBot;

    public TestCommand(DiscordBot discordBot) {
        super("test", "testing", "tester");
        this.discordBot = discordBot;
    }


    @Override
    public void execute(CommandSender sender, Command command) {
        try {
            throw new Exception("Does the exception tracker work? yes it does!");
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
