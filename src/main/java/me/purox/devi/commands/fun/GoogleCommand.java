package me.purox.devi.commands.fun;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GoogleCommand extends ICommand {

    private Devi devi;

    public GoogleCommand(Devi devi) {
        super("google", "lmgtfy", "lmgify");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length < 1) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 577) + " " + command.getPrefix() + "google <searchterm>");
            return;
        }

        String searchterm = Arrays.stream(command.getArgs()).skip(0).collect(Collectors.joining("+"));
        String URL = "http://lmgtfy.com/?q=" + searchterm;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor(devi.getTranslation(command.getLanguage(), 578), null, "https://i.imgur.com/BWlbFx3.png");
        builder.setColor(Color.decode("#008744"));
        builder.setDescription(URL);
        sender.reply(builder.build());
    }
}
