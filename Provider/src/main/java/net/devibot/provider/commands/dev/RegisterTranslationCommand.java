package net.devibot.provider.commands.dev;

import net.devibot.core.entities.Emote;
import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RegisterTranslationCommand extends ICommand {

    private DiscordBot discordBot;

    public RegisterTranslationCommand(DiscordBot discordBot) {
        super("registertranslation");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length < 2) {
            sender.reply(Emote.ERROR + " | Correct Usage: `" + command.getPrefix() + "registertranslation <key> <text>`");
            return;
        }

        String key = command.getArgs()[0];
        String text = Arrays.stream(command.getArgs()).skip(1).collect(Collectors.joining(" "));

        discordBot.getMainframeManager().registerTranslation(key, text, success -> {
            if (!success) {
                sender.reply(Emote.ERROR + " | Database update was not acknowledged!");
            } else {
                sender.reply(Emote.SUCCESS + " | Translation with key `" + key + "` was registered successfully!");
            }
        });
    }
}
