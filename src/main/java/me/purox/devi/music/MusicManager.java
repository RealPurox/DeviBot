package me.purox.devi.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class MusicManager {

    private Devi devi;
    private final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private final Map<Guild, Map.Entry<AudioPlayer, TrackManager>> audioPlayer = new HashMap<>();

    public MusicManager (Devi devi) {
        AudioSourceManagers.registerRemoteSources(manager);
        this.devi = devi;
    }

    private AudioPlayer createPlayer(Guild guild) {
        AudioPlayer player = manager.createPlayer();
        TrackManager trackManager = new TrackManager(devi, player);

        player.addListener(trackManager);
        player.setVolume(35);

        guild.getAudioManager().setSendingHandler(new PlayerSendHandler(player));
        audioPlayer.put(guild, new AbstractMap.SimpleEntry<>(player, trackManager));

        return player;
    }

    private boolean hasPlayer(Guild guild) {
        return audioPlayer.containsKey(guild);
    }

    public AudioPlayer getPlayer(Guild guild) {
        if (hasPlayer(guild)) return audioPlayer.get(guild).getKey();
        else return createPlayer(guild);
    }

    public TrackManager getManager(Guild guild) {
        if (!audioPlayer.containsKey(guild)) createPlayer(guild);
        return audioPlayer.get(guild).getValue();
    }

    public boolean isIdle(Guild guild) {
        return !hasPlayer(guild) || getPlayer(guild).getPlayingTrack() == null;
    }

    public void loadTrack(MessageReceivedEvent event, String identifier, VoiceChannel channel) {
        Guild guild = channel.getGuild();
        DeviGuild deviGuild = devi.getDeviGuild(guild.getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

        getPlayer(guild);

        manager.setFrameBufferDuration(5000);
        manager.loadItemOrdered(guild, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                getManager(guild).addToQueue(audioTrack, channel);
                MessageUtils.sendMessage(event.getChannel(), new EmbedBuilder()
                        .setColor(new Color(34, 113, 126))
                        .setAuthor(devi.getTranslation(language, 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg")
                        .setDescription("**" + devi.getTranslation(language, 92) + "**")
                        .addField(devi.getTranslation(language, 87), audioTrack.getInfo().title, false)
                        .addField(devi.getTranslation(language, 88), audioTrack.getInfo().author, false)
                        .addField(devi.getTranslation(language, 89), getTimestamp(audioTrack.getInfo().length), false)
                        .build());

            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                if (audioPlaylist.isSearchResult()){
                    trackLoaded(audioPlaylist.getTracks().get(0));
                } else {
                    long length = 0;
                    for (AudioTrack track : audioPlaylist.getTracks()) {
                        getManager(guild).addToQueue(track, channel);
                        length += track.getDuration();
                    }
                    MessageUtils.sendMessage(event.getChannel(), new EmbedBuilder()
                            .setColor(new Color(34, 113, 126))
                            .setAuthor(devi.getTranslation(language, 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg")
                            .setDescription("**" + devi.getTranslation(language, 93) + "**")
                            .addField(devi.getTranslation(language, 94), audioPlaylist.getName(), false)
                            .addField(devi.getTranslation(language, 95), String.valueOf(audioPlaylist.getTracks().size()), false)
                            .addField(devi.getTranslation(language, 89), getTimestamp(length), false)
                            .build());
                }
            }

            @Override
            public void noMatches() {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 96, identifier));
            }

            @Override
            public void loadFailed(FriendlyException e) {
                MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 97, identifier) + "\n(" + e.getMessage() + ")");
            }
        });
    }

    public void skip(Guild g) {
        getPlayer(g).stopTrack();
    }

    public String getTimestamp(long milis) {
        long seconds = milis / 1000;
        long hours = Math.floorDiv(seconds, 3600);
        seconds = seconds - (hours * 3600);
        long minutes = Math.floorDiv(seconds, 60);
        seconds = seconds - (minutes * 60);
        return (hours == 0 ? "" : (hours < 100 ? String.format("%02d", hours) : hours + ":")) + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }


    public String buildQueueMessage(AudioInfo info, Language language) {
        AudioTrackInfo trackInfo = info.getTrack().getInfo();
        String message = "**" + trackInfo.title + "**\n";
        message += " - " + devi.getTranslation(language, 98) + ": " + getTimestamp(trackInfo.length) + "\n";
        message += " - " + devi.getTranslation(language, 88) + ": " + trackInfo.author + "\n";
        return message + "\n";
    }
}
