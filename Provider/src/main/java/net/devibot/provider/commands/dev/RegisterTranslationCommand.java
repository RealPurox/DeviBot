package net.devibot.provider.commands.dev;

import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RegisterTranslationCommand extends ICommand {

    private DiscordBot discordBot;

    public RegisterTranslationCommand(DiscordBot discordBot) {
        super("registertranslation", "regtrans");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length < 3) {
            sender.errorMessage().append("Correct Usage: `").append(command.getPrefix()).append("registertranslation <old id> <key> <text>`").execute();
            return;
        }

        int oldId;

        try {
            oldId = Integer.valueOf(command.getArgs()[0]);
        } catch (NumberFormatException e) {
            sender.errorMessage().append("`").append(command.getArgs()[0]).append("is not a valid id. Use -1 to use no old id.").execute();
            return;
        }

        String key = oldId == -1 ? command.getArgs()[1] : command.getArgs()[1];
        String text = Arrays.stream(command.getArgs()).skip(2).collect(Collectors.joining(" "));

        discordBot.getMainframeManager().registerTranslation(key, text, oldId, success -> {
            if (!success) {
                sender.errorMessage().append("Database update was not acknowledged!").execute();
            } else {
                sender.successMessage().append("Translation with key `").append(key).append("` was registered successfully!\n\n").append("Text: ").append(text).execute();
            }
        });
    }
}
