package me.purox.devi.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.User;

public class AudioInfo {

    private final AudioTrack audioTrack;
    private final User requester;

    public AudioInfo(AudioTrack audioTrack, User requester) {
        this.audioTrack = audioTrack;
        this.requester = requester;
    }

    public AudioTrack getAudioTrack() {
        return audioTrack;
    }

    public User getRequester() {
        return requester;
    }

    public AudioInfo createNew() {
        return new AudioInfo(audioTrack.makeClone(), requester);
    }
}
