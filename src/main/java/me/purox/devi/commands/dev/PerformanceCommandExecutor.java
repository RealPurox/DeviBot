package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
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
        int seconds = (int) (millis / 1000) % 60 ;
        int minutes = (int) ((millis / (1000*60)) % 60);
        int hours   = (int) ((millis / (1000*60*60)) % 24);

        EmbedBuilder builder = new EmbedBuilder().setAuthor("Performance").setColor(Color.RED);
        builder.addField("Threads", String.valueOf(threads), false);
        builder.addField("Using Memory (MB)", String.valueOf(usingMemory), false);
        builder.addField("Free Memory (MB)", String.valueOf(freeMemory), false);
        builder.addField("Allocated Memory (MB)", String.valueOf(allocatedMemory), false);
        builder.addField("Max Memory (MB)", String.valueOf(maxMemory), false);
        builder.addField("Music Player" , String.valueOf(devi.getMusicManager().getAudioPlayers().size()), false);
        builder.addField("Commands Executed", String.valueOf(devi.getCommandsExecuted()), false);
        builder.addField("Songs Played", String.valueOf(devi.getSongsPlayed()), false);
        builder.addField("Uptime", hours + " hours, " + minutes + " minutes, " + seconds + " seconds", false);

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
