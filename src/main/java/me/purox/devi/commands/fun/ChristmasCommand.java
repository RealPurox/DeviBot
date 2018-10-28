package me.purox.devi.commands.fun;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.AnimatedEmote;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ChristmasCommand extends ICommand {

    private Devi devi;

    public ChristmasCommand(Devi devi) {
        super("christmas");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, Calendar.DECEMBER, 25, 0, 0, 0);

        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-dd-MM hh:mm:ss a");
        EmbedBuilder embedBuilder = new EmbedBuilder();

        long time = TimeUnit.MILLISECONDS.toDays(calendar.getTimeInMillis() - System.currentTimeMillis());

        embedBuilder.setColor(Color.decode("#d42426"));
        embedBuilder.setTitle(devi.getTranslation(command.getLanguage(), 590) + " " + new AnimatedEmote(devi).CristmasParrotEmote().getAsMention());
        embedBuilder.setDescription(devi.getTranslation(command.getLanguage(), 591, "**" + time + "**") + "\n" +
                devi.getTranslation(command.getLanguage(), 592) + ": " + dateTimeFormatter.format(calendar.getTime()));

        sender.reply(embedBuilder.build());
    }
}
