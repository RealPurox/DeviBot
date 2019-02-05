package net.devibot.provider.commands.dev;

import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.dv8tion.jda.core.EmbedBuilder;
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
        sender.successMessage().appendTranslation("commands.test.success").execute();
        sender.errorMessage().append("Error message template").execute();
        sender.infoMessage().append("Info message template").execute();
        sender.message().append("Message without template").execute();
        sender.message().setEmbed(new EmbedBuilder().setDescription("Message with embed only").build()).execute();
        sender.message().setEmbed(new EmbedBuilder().setDescription("Message with embed only").build())
            .append("Message with embed and text!").execute();
    }
}
