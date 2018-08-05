package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.TimeUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.util.List;

public class StatsCommandExecutor implements CommandExecutor {

    private Devi devi;

    public StatsCommandExecutor(Devi devi){
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {

        Devi.Stats stats = devi.getCurrentStats();

        String pfpUrl = "https://cdn.discordapp.com/avatars/354361427731152907/0dddb9e6f92b0c338e780ced51077239.png";

        long millis = ManagementFactory.getRuntimeMXBean().getUptime();

        String uptime = TimeUtils.toRelative(millis);
        uptime = uptime.substring(0, uptime.length() - 3);

        EmbedBuilder em = new EmbedBuilder();
        em.setAuthor("Devi", null, pfpUrl);
        em.setColor(Color.decode("#7289da"));
        em.setThumbnail(pfpUrl);

        em.addField(devi.getTranslation(command.getLanguage(), 545), stats.getGuilds() + " " + devi.getTranslation(command.getLanguage(), 554), true); // guilds
        em.addField(devi.getTranslation(command.getLanguage(), 546), stats.getUsers() + " " + devi.getTranslation(command.getLanguage(), 553), true); // users
        em.addField(devi.getTranslation(command.getLanguage(), 547), stats.getChannels() + " " + devi.getTranslation(command.getLanguage(), 437), true); // channels
        em.addField(devi.getTranslation(command.getLanguage(), 551), String.valueOf(devi.getMusicManager().getGuildPlayers().size()), true); // music players
        em.addField(devi.getTranslation(command.getLanguage(), 552), devi.getShardManager().getAveragePing() + " " + devi.getTranslation(command.getLanguage(), 555), true); // response time
        em.addField(devi.getTranslation(command.getLanguage(), 548), devi.getTranslation(command.getLanguage(), 549), true); // website
        em.addField(devi.getTranslation(command.getLanguage(), 556), uptime, false); // uptime

        sender.reply(em.build());

    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 550;
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
        return ModuleType.INFO_COMMANDS;
    }
}
