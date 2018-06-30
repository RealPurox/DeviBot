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

    public boolean isEqualTo(AudioInfo audioInfo) {
        return this.audioTrack.getInfo().author.equals(audioInfo.getAudioTrack().getInfo().author) && this.audioTrack.getInfo().title.equals(audioInfo.getAudioTrack().getInfo().title) &&
                this.audioTrack.getInfo().identifier.equals(audioInfo.getAudioTrack().getInfo().identifier) && this.audioTrack.getInfo().length == audioInfo.getAudioTrack().getInfo().length &&
                this.audioTrack.getInfo().uri.equals(audioInfo.getAudioTrack().getInfo().uri) && audioInfo.getRequester().getIdLong() == this.requester.getIdLong();
    }

    @Override
    public String toString() {
        return "[AudioInfo: Title:" + audioTrack.getInfo().title + "|| URI:" + audioTrack.getInfo().uri + "|| Requester: " + requester.getName() + "#" + requester.getDiscriminator() + "]";
    }
}
