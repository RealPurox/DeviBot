package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
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


        EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#36393E"));
        builder.addField("Threads", String.valueOf(threads), true);
        builder.addField("Using Memory", getUsedRam() + " MB", true);
        builder.addField("Free Memory", getFreeRam() + " MB", true);
        builder.addField("Total Memory", getTotalRam() + " MB", true);
        builder.addField("Max Memory", getMaxRam() + " MB", true);
        builder.addField("Registered Music Players" , String.valueOf(devi.getMusicManager().getGuildPlayers().size()), true);
        builder.addField("Audio Connections" , String.valueOf(audioConnections.get()), true);
        builder.addField("Commands Executed", String.valueOf(devi.getCommandsExecuted()), true);
        builder.addField("Songs Played", String.valueOf(devi.getSongsPlayed()), true);
        builder.addField("Server Status", getServerStatus(), true);
        builder.addField("Response time", devi.getShardManager().getAveragePing() + " ms", true);
        builder.addField("Uptime", uptime, false);

        sender.reply(builder.build());
    }

    private String getServerStatus() {
        int totalMem = getTotalRam();
        int usedMem = totalMem - getFreeRam();

        int points = 0;
        if ((totalMem >= getMaxRam()- 200) && (totalMem / 2 <= usedMem)) {
            points += 10;
            if (totalMem * 0.7D <= usedMem) {
                points += 10;
                if (totalMem * 0.9D <= usedMem) {
                    points += 10;
                }
            }
        }
        String status = DeviEmote.SUCCESS.get() + " Good";
        if (points >= 30) {
            status = ":warning: Critical";
        } else if (points >= 20) {
            status = DeviEmote.ERROR.get() + " Low";
        }
        return status;
    }

    private int getFreeRam() {
        Runtime runtime = Runtime.getRuntime();
        return Math.round((float)(runtime.freeMemory() / 1048576L));
    }

    private int getMaxRam() {
        Runtime runtime = Runtime.getRuntime();
        return Math.round((float)(runtime.maxMemory() / 1048576L));
    }

    private int getUsedRam() {
        return getTotalRam() - getFreeRam();
    }

    private int getTotalRam() {
        Runtime runtime = Runtime.getRuntime();
        return Math.round((float)(runtime.totalMemory() / 1048576L));
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
