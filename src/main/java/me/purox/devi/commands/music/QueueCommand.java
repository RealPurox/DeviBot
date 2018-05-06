package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.music.AudioInfo;
import me.purox.devi.utils.JavaUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QueueCommand implements Command {

    private Devi devi;
    public QueueCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String command, String[] args, MessageReceivedEvent event) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(34, 113, 126));
        builder.setAuthor(devi.getTranslation(language, 118), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg");

        List<AudioInfo> raw = new ArrayList<>(devi.getMusicManager().getManager(event.getGuild()).getQueue());
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

        String sb = "";
        sb += devi.getTranslation(language, 119) + ": " + devi.getMusicManager().getTimestamp(raw.stream().mapToLong(info -> info.getTrack().getDuration()).sum()) + "\n";
        sb += devi.getTranslation(language, 120) + ": " + raw.size() + "\n\n";
        if (!pages.isEmpty()) {
            for (AudioInfo audioInfo : pages.get(page)) {
                sb += devi.getMusicManager().buildQueueMessage(audioInfo, language);
            }
        }
        builder.setDescription(sb);
        builder.setFooter(devi.getTranslation(language, 121, (page + 1), totalPages == 0 ? 1 : totalPages, prefix + "queue, <page>"), null);
        MessageUtils.sendMessage(event.getChannel(), builder.build());
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
