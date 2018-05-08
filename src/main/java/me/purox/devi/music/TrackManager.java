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
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
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
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        sendMusicLog(voiceChannel.getGuild(), new EmbedBuilder()
                .setColor(new Color(34, 113, 126))
                .setAuthor(devi.getTranslation(language, 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg")
                .setDescription("**" + devi.getTranslation(language, 86) + "**")
                .addField(devi.getTranslation(language, 87), track.getInfo().title, false)
                .addField(devi.getTranslation(language, 88), track.getInfo().author, false)
                .addField(devi.getTranslation(language, 89), devi.getMusicManager().getTimestamp(track.getInfo().length), false)
                .addField(devi.getTranslation(language, 90), "[" + devi.getTranslation(language, 91) + "](" + track.getInfo().uri + ")", false)
                .build());
    }


    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        AudioInfo current = queue.peek();
        if (current != null) {
            queue.remove();
            if (!queue.isEmpty())
                player.playTrack(queue.element().getTrack());
            else {
                DeviGuild deviGuild = devi.getDeviGuild(current.getChannel().getGuild().getId());
                Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
                EmbedBuilder builder = new EmbedBuilder()
                        .setColor(new Color(34, 113, 126))
                        .setAuthor(devi.getTranslation(language, 85), null, "https://i.pinimg.com/736x/9d/83/17/9d8317162494a004969b79c85d88b5c1--music-logo-dj-music.jpg");
                if (current.getChannel().getMembers().size() > 1) {
                    builder.setDescription("**" + devi.getTranslation(language, 136) + "**");
                } else {
                    builder.setDescription("**" + devi.getTranslation(language, 144) + "**");
                    leaveChannel(current.getChannel().getGuild());
                }
                sendMusicLog(current.getChannel().getGuild(), builder.build());
            }
        }
    }

    private void sendMusicLog(Guild guild, MessageEmbed embed) {
        DeviGuild deviGuild = devi.getDeviGuild(guild.getId());
        if (deviGuild.getSettings().getBooleanValue(GuildSettings.Settings.MUSIC_LOG_ENABLED)) {
            TextChannel channel = guild.getTextChannelById(deviGuild.getSettings().getStringValue(GuildSettings.Settings.MUSIC_LOG_CHANNEL));
            if (channel != null) {
                MessageUtils.sendMessage(channel, embed);
            }
        }
    }

    public void leaveChannel(Guild guild) {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                devi.getMusicManager().getManager(guild).clearQueue();
                devi.getMusicManager().getAudioPlayers().remove(guild);
                guild.getAudioManager().closeAudioConnection();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
