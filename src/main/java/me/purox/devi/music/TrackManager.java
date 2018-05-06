package me.purox.devi.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackManager extends AudioEventAdapter {

    private final AudioPlayer audioPlayer;
    private final Queue<AudioInfo> queue;
    private Devi devi;

    public TrackManager(Devi devi, AudioPlayer audioPlayer) {
        this.devi = devi;
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void addToQueue(AudioTrack track, VoiceChannel voiceChannel){
        AudioInfo audioInfo = new AudioInfo(track, voiceChannel);
        queue.add(audioInfo);

        if (audioPlayer.getPlayingTrack() == null) {
            audioPlayer.playTrack(track);
        }
    }

    public Set<AudioInfo> getQueue() {
        return new LinkedHashSet<>(this.queue);
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public AudioInfo getAudioInfo(AudioTrack track){
        return this.queue.stream().filter(audioInfo -> audioInfo.getTrack().equals(track)).findFirst().orElse(null);
    }

    public void clearQueue() {
        this.queue.clear();
    }

    public void shuffleQueue() {
        List<AudioInfo> currentQueue = new ArrayList<>(this.queue);
        AudioInfo currentAudioInfo = currentQueue.get(0);
        currentQueue.remove(0);
        Collections.shuffle(currentQueue);
        currentQueue.add(0, currentAudioInfo);
        clearQueue();
        this.queue.addAll(currentQueue);
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        AudioInfo info = this.queue.element();
        VoiceChannel voiceChannel = info.getChannel();
        info.getChannel().getGuild().getAudioManager().openAudioConnection(voiceChannel);

        DeviGuild deviGuild = devi.getDeviGuild(voiceChannel.getGuild().getId());
        if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MUSIC_LOG_ENABLED)) {
            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
            TextChannel channel = voiceChannel.getGuild().getTextChannelById(deviGuild.getSettings().getStringValue(GuildSettings.Settings.MUSIC_LOG_CHANNEL));
            if (channel != null) {
                MessageUtils.sendMessage(channel, new EmbedBuilder()
                        .setColor(new Color(34, 113, 126))
                        .setAuthor(devi.getTranslation(language, 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg")
                        .setDescription("**" + devi.getTranslation(language, 86) + "**")
                        .addField(devi.getTranslation(language, 87), track.getInfo().title, false)
                        .addField(devi.getTranslation(language, 88), track.getInfo().author, false)
                        .addField(devi.getTranslation(language, 89), devi.getMusicManager().getTimestamp(track.getInfo().length), false)
                        .addField(devi.getTranslation(language, 90), "[" + devi.getTranslation(language, 91) + "](" + track.getInfo().uri + ")", false)
                        .build());
            }
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        queue.remove();
        if (!queue.isEmpty())
            player.playTrack(queue.element().getTrack());
    }
}
