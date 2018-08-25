package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChristmasCommandExecutor implements CommandExecutor {

    private Devi devi;

    public ChristmasCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-dd-MM hh:mm:ss a");


        EmbedBuilder embedBuilder = new EmbedBuilder();

            c.set(2018, Calendar.DECEMBER, 25, 0, 0, 0);

            long time = TimeUnit.MILLISECONDS.toDays(c.getTimeInMillis() - System.currentTimeMillis());

            embedBuilder.setColor(Color.decode("#d42426"));
            embedBuilder.setTitle(devi.getTranslation(command.getLanguage(), 590));
            embedBuilder.setDescription(devi.getTranslation(command.getLanguage(), 591, "**" + time + "**") + "\n" +
                    devi.getTranslation(command.getLanguage(), 592) + ": " + dateTimeFormatter.format(c.getTime()));
            sender.reply(embedBuilder.build());
        }


    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 593;
    }

    @Override
    public List<String> getAliases() {
        return null;
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
