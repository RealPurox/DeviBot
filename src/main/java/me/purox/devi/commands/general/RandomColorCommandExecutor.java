package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomColorCommandExecutor implements CommandExecutor {


    private Devi devi;
    public RandomColorCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {

        Random random = new Random();

        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();

        Color randomColor = new Color(r, g ,b);

        String hexValue = "#" + Integer.toHexString(randomColor.getRGB()).substring(2);

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(devi.getTranslation(command.getLanguage(), 405));
        embed.addField("HEX", hexValue, false);
        embed.addField("RGB", "R: " + randomColor.getRed() + " G: " + randomColor.getGreen() + " B: " + randomColor.getBlue(), false);
        embed.setColor(randomColor.getRGB());

        MessageEmbed build = embed.build();

        sender.reply(build);
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 404;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("rcolor");
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.FUN_COMMANDS;
    }
}
