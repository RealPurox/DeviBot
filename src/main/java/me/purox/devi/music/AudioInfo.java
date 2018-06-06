package me.purox.devi.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class AudioInfo {

    private final AudioTrack track;
    private final VoiceChannel channel;


    AudioInfo(AudioTrack track, VoiceChannel channel) {
        this.track = track;
        this.channel = channel;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public VoiceChannel getChannel() {
        return channel;
    }
}
