package me.purox.devi.commands.admin;

import com.sun.management.OperatingSystemMXBean;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.utils.TimeUtils;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceCommand extends ICommand {

    private Devi devi;

    public PerformanceCommand(Devi devi) {
        super("performance");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (!devi.getAdmins().contains(sender.getId())) return;

        long millis = ManagementFactory.getRuntimeMXBean().getUptime();
        int threads = Thread.activeCount();

        String uptime = TimeUtils.toRelative(millis);
        uptime = uptime.substring(0, uptime.length() - 3);

        AtomicLong audioConnections = new AtomicLong();
        devi.getShardManager().getShards().forEach(jda -> jda.getAudioManagers().forEach(audioManager -> { if (audioManager.isConnected())
            audioConnections.getAndIncrement();
        }));
        OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        double cpuLoad = osBean.getSystemCpuLoad() * 100;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#36393E"));
        builder.addField("Threads", String.valueOf(threads), true);
        builder.addField("CPU Usage",  decimalFormat.format(cpuLoad) + "%", true);
        builder.addField("Server Status", getServerStatus(), true);

        builder.addField("Memory", getUsedRam() + "MB [" + getRamBar() + "] " + getTotalRam() + " MB (" + getRamPercent() + "%)", false);

        builder.addField("Audio Connections" , String.valueOf(audioConnections.get()), true);
        builder.addField("Commands Executed", String.valueOf(devi.getCommandsExecuted()), true);
        builder.addField("Songs Played", String.valueOf(devi.getSongsPlayed()), true);
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
        String status = "Good " + Emote.SUCCESS.get();
        if (points >= 30) {
            status = "Critical :warning:";
        } else if (points >= 20) {
            status = "Low " + Emote.ERROR.get();
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

    private String getRamBar() {
        StringBuilder ram = new StringBuilder("[|");
        int percent = getRamPercent();

        for (int i = 0; i < percent / 2; i++) {
            ram.append("|");
        }
        ram.append("](https://www.devibot.net/)");
        for (int i = 0; i < 50 - percent / 2; i++) {
            ram.append("|");
        }

        return ram.toString();
    }

    private int getRamPercent() {
        return (int) ((double) getUsedRam() / (double) getTotalRam() * 100);
    }
}
