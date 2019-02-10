package net.devibot.provider.commands.dev;

import com.sun.management.OperatingSystemMXBean;
import net.devibot.core.utils.TimeUtils;
import net.devibot.provider.commands.CommandSender;
import net.devibot.provider.commands.ICommand;
import net.devibot.provider.core.DiscordBot;
import net.devibot.core.entities.Emote;
import net.dv8tion.jda.core.EmbedBuilder;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicLong;

public class PerformanceCommand extends ICommand {

    private DiscordBot discordBot;

    public PerformanceCommand(DiscordBot discordBot) {
        super("performance");
        this.discordBot = discordBot;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        long millis = ManagementFactory.getRuntimeMXBean().getUptime();
        int threads = Thread.activeCount();

        String uptime = TimeUtils.toRelative(millis);
        uptime = uptime.substring(0, uptime.length() - 3);

        AtomicLong audioConnections = new AtomicLong();
        discordBot.getShardManager().getShards().forEach(jda -> jda.getAudioManagers().forEach(audioManager -> { if (audioManager.isConnected())
            audioConnections.getAndIncrement();
        }));

        OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        double cpuLoad = osBean.getSystemCpuLoad() * 100;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        EmbedBuilder builder = new EmbedBuilder().setColor(0x36393E);
        builder.addField("Threads", String.valueOf(threads), true);
        builder.addField("CPU Usage",  decimalFormat.format(cpuLoad) + "%", true);
        builder.addField("Server Status", getServerStatus(), true);

        builder.addField("Memory", getUsedRam() + "MB [" + getRamBar() + "] " + getTotalRam() + " MB (" + getRamPercent() + "%)", false);

        builder.addField("Audio Connections" , String.valueOf(audioConnections.get()), true);
        builder.addField("Response time", discordBot.getShardManager().getAveragePing() + " ms", true);
        builder.addField("Uptime", uptime, false);

        sender.message().setEmbed(builder.build()).execute();
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
        StringBuilder ram = new StringBuilder("[‖");
        int percent = getRamPercent();

        for (int i = 0; i < percent / 4; i++) {
            ram.append("‖");
        }
        ram.append("](https://www.devibot.net/)");
        for (int i = 0; i < 25 - percent / 2; i++) {
            ram.append("‖");
        }

        return ram.toString();
    }

    private int getRamPercent() {
        return (int) ((double) getUsedRam() / (double) getTotalRam() * 100);
    }
}
