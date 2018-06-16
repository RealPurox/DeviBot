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

        int threads = Thread.activeCount();
        long usingMemory = ((runtime.totalMemory() - runtime.freeMemory()) / mb);
        long freeMemory = runtime.freeMemory() / mb;
        long allocatedMemory = runtime.totalMemory() / mb;
        long maxMemory = runtime.maxMemory() / mb;

        long millis = ManagementFactory.getRuntimeMXBean().getUptime();

        EmbedBuilder builder = new EmbedBuilder().setAuthor("Performance").setColor(new Color(38, 169, 213));
        builder.addField("Threads", String.valueOf(threads), true);
        builder.addField("Using Memory (MB)", String.valueOf(usingMemory), true);
        builder.addField("Free Memory (MB)", String.valueOf(freeMemory), true);
        builder.addField("Allocated Memory (MB)", String.valueOf(allocatedMemory), true);
        builder.addField("Max Memory (MB)", String.valueOf(maxMemory), true);
        builder.addField("Music Player" , String.valueOf(devi.getMusicManager().getAudioPlayers().size()), true);
        builder.addField("Commands Executed", String.valueOf(devi.getCommandsExecuted()), true);
        builder.addField("Songs Played", String.valueOf(devi.getSongsPlayed()), true);
        builder.addField("Uptime", "Devi was booted " + TimeUtils.toRelative(millis), false);

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
