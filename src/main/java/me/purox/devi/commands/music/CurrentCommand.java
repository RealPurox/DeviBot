package me.purox.devi.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class CurrentCommand implements Command {

    private Devi devi;
    public CurrentCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String command, String[] args, MessageReceivedEvent event) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

        AudioPlayer player = devi.getMusicManager().getPlayer(event.getGuild());
        if (player.getPlayingTrack() == null) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 129));
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(34, 113, 126));
        builder.setAuthor(devi.getTranslation(language, 130), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg");
        builder.addField(devi.getTranslation(language, 87), player.getPlayingTrack().getInfo().title, false);
        builder.addField(devi.getTranslation(language, 98), devi.getMusicManager().getTimestamp(player.getPlayingTrack().getPosition(), language) + "/" + devi.getMusicManager().getTimestamp(player.getPlayingTrack().getDuration(), language), false);

        MessageUtils.sendMessage(event.getChannel(), builder.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 128;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("playing", "nowplaying");
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
