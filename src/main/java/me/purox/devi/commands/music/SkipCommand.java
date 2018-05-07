package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkipCommand implements Command {


    private Devi devi;
    private HashMap<Guild, Map.Entry<Integer, Integer>> guildVotes = new HashMap<>();

    public SkipCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String command, String[] args, MessageReceivedEvent event) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

        if (devi.getMusicManager().isIdle(event.getGuild()) || devi.getMusicManager().getPlayer(event.getGuild()).isPaused()) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 129));
            return;
        }

        if (!event.getMember().getVoiceState().inVoiceChannel()) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 100));
            return;
        }

        int amount;
        try {
            amount = args.length == 0 ? 1 : Integer.parseInt(args[0]);
        } catch (NumberFormatException e){
            amount = -1;
        }

        if (amount > 10) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 133));
            return;
        }

        String oldTrack = devi.getMusicManager().getPlayer(event.getGuild()).getPlayingTrack().getInfo().title;

        for (int i = 0; i < amount; i++) {
            devi.getMusicManager().skip(event.getGuild());
        }

        String newTrack = devi.getMusicManager().getPlayer(event.getGuild()).getPlayingTrack() != null ? devi.getMusicManager().getPlayer(event.getGuild()).getPlayingTrack().getInfo().title : "QUEUE_END";

        EmbedBuilder builder = new EmbedBuilder().setColor(new Color(34, 113, 126));
        builder.setAuthor(devi.getTranslation(language, 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg");

        if (amount == 1) builder.setDescription(":white_check_mark:" + devi.getTranslation(language, 134, "`" + oldTrack + "`"));
        else builder.setDescription(":white_check_mark:" + devi.getTranslation(language, 135, amount));

        if (newTrack.equals("QUEUE_END")) builder.appendDescription("\n:track_next: " + devi.getTranslation(language, 136));
        else builder.appendDescription("\n:track_next: " + devi.getTranslation(language, 137, "`" + newTrack + "`"));

        MessageUtils.sendMessage(event.getChannel(), builder.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 131;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
