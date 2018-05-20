package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.music.AudioInfo;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QueueCommandExecutor implements CommandExecutor {

    private Devi devi;
    public QueueCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(34, 113, 126));
        builder.setAuthor(devi.getTranslation(command.getLanguage(), 118), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg");

        List<AudioInfo> raw = new ArrayList<>(devi.getMusicManager().getManager(command.getEvent().getGuild()).getQueue());
        List<List<AudioInfo>> pages = JavaUtils.chopList(raw, 5);

        int page;
        try {
            page = args.length > 0 ? (Integer.parseInt(args[0]) - 1) : 0;
        } catch (NumberFormatException e) {
            page = 0;
        }

        int totalPages = pages.size();

        if (page > (totalPages - 1)) page = totalPages - 1;
        if (page < 1) page = 0;

        StringBuilder sb = new StringBuilder();
        sb.append(devi.getTranslation(command.getLanguage(), 119)).append(": ").append(devi.getMusicManager().getTimestamp(raw.stream().mapToLong(info -> info.getTrack().getDuration()).sum(), command.getLanguage())).append("\n");
        sb.append(devi.getTranslation(command.getLanguage(), 120)).append(": ").append(raw.size()).append("\n\n");
        if (!pages.isEmpty()) {
            for (AudioInfo audioInfo : pages.get(page)) {
                sb.append(devi.getMusicManager().buildQueueMessage(audioInfo, command.getLanguage()));
            }
        }
        builder.setDescription(sb.toString());
        builder.setFooter(devi.getTranslation(command.getLanguage(), 121, (page + 1), totalPages == 0 ? 1 : totalPages, command.getPrefix() + "queue, <page>"), null);
        sender.reply(builder.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 117;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
