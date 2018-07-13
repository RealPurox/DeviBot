package me.purox.devi.commands.dev;

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
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceCommandExecutor implements CommandExecutor {

    private Devi devi;

    public PerformanceCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!devi.getAdmins().contains(sender.getId()) && !sender.isConsoleCommandSender()) return;

        Runtime runtime = Runtime.getRuntime();
        int mb = 1024 * 1024;

        long millis = ManagementFactory.getRuntimeMXBean().getUptime();

        int threads = Thread.activeCount();
        long usingMemory = ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        long freeMemory = runtime.freeMemory() / mb;

        String uptime = TimeUtils.toRelative(millis);
        uptime = uptime.substring(0, uptime.length() - 3);

        AtomicLong audioConnections = new AtomicLong();
        devi.getShardManager().getShards().forEach(jda -> jda.getAudioManagers().forEach(audioManager -> { if (audioManager.isConnected())
            audioConnections.getAndIncrement();
        }));

        Devi.Stats stats = devi.getCurrentStats();
        EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#36393E"));
        builder.addField("Threads", String.valueOf(threads), true);
        builder.addField("Using Memory", String.valueOf(usingMemory), true);
        builder.addField("Free Memory (MB)", String.valueOf(freeMemory), true);
        builder.addField("Registered Music Players" , String.valueOf(devi.getMusicManager().getAudioPlayers().size()), true);
        builder.addField("Audio Connections" , String.valueOf(audioConnections.get()), true);
        builder.addField("Commands Executed", String.valueOf(devi.getCommandsExecuted()), true);
        builder.addField("Songs Played", String.valueOf(devi.getSongsPlayed()), true);
        builder.addField("Total Guilds", String.valueOf(stats.getGuilds()), true);
        builder.addField("Total Users", String.valueOf(stats.getUsers()), true);
        builder.addField("Total Channels", String.valueOf(stats.getChannels()), true);
        builder.addField("Ping", String.valueOf(stats.getPing()), true);
        builder.addField("Uptime", uptime, false);

        sender.reply(builder.build());
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 0;
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
        return ModuleType.DEV;
    }
}
