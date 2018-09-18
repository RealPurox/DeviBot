package me.purox.devi.punishments.options;

import net.dv8tion.jda.core.entities.VoiceChannel;

public class VoiceKickOptions implements Options {

    private VoiceChannel channel;

    public VoiceKickOptions setChannel(VoiceChannel channel) {
        this.channel = channel;
        return this;
    }

    public VoiceChannel getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "Channel: " + channel;
    }
}
