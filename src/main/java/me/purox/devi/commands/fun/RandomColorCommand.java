package me.purox.devi.commands.fun;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.Random;

public class RandomColorCommand extends ICommand {

    private Devi devi;

    public RandomColorCommand(Devi devi) {
        super("randomcolor", "rcolor");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        Random random = new Random();
        Color randomColor = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
        String hexValue = "#" + Integer.toHexString(randomColor.getRGB()).substring(2);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(devi.getTranslation(command.getLanguage(), 405));
        embed.addField("HEX", hexValue, false);
        embed.addField("RGB", "R: " + randomColor.getRed() + " G: " + randomColor.getGreen() + " B: " + randomColor.getBlue(), false);
        embed.setColor(randomColor);

        sender.reply(embed.build());
    }
}
