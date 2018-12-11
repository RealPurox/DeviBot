package me.purox.devi.commands.info;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.utils.TimeUtils;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.lang.management.ManagementFactory;

public class StatsCommand extends ICommand {

    private Devi devi;

    public StatsCommand(Devi devi) {
        super("stats");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        Devi.Stats stats = devi.getCurrentStats();

        long millis = ManagementFactory.getRuntimeMXBean().getUptime();

        String uptime = TimeUtils.toRelative(millis);
        uptime = uptime.substring(0, uptime.length() - 3);

        EmbedBuilder em = new EmbedBuilder();
        em.setAuthor("Devi", null, command.getJDA().getSelfUser().getAvatarUrl());
        em.setColor(devi.getColor());
        em.setThumbnail(command.getJDA().getSelfUser().getAvatarUrl());

        em.addField(devi.getTranslation(command.getLanguage(), 545), stats.getGuilds() + " " + devi.getTranslation(command.getLanguage(), 554), true); // guilds
        em.addField(devi.getTranslation(command.getLanguage(), 546), stats.getUsers() + " " + devi.getTranslation(command.getLanguage(), 553), true); // users
        em.addField(devi.getTranslation(command.getLanguage(), 547), stats.getChannels() + " " + devi.getTranslation(command.getLanguage(), 437), true); // channels
        em.addField(devi.getTranslation(command.getLanguage(), 551), String.valueOf(devi.getMusicManager().getGuildPlayers().size()), true); // music players
        em.addField(devi.getTranslation(command.getLanguage(), 552), devi.getShardManager().getAveragePing() + " " + devi.getTranslation(command.getLanguage(), 555), true); // response time
        em.addField(devi.getTranslation(command.getLanguage(), 548), devi.getTranslation(command.getLanguage(), 549), true); // website
        em.addField(devi.getTranslation(command.getLanguage(), 556), uptime, false); // uptime

        sender.reply(em.build());
    }
}
