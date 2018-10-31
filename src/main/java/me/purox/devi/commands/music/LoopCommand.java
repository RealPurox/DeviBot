package me.purox.devi.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.music.GuildPlayer;

public class LoopCommand extends ICommand {

    private Devi devi;

    public LoopCommand(Devi devi) {
        super("loop");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getGuild());

        if (!devi.getMusicManager().isDJorAlone(command.getMember(), command.getGuild().getMember(sender).getVoiceState().getChannel(), command.getGuild())) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 454));
            return;
        }

        if (guildPlayer.getLoopedTrack() == null) {
            if (guildPlayer.getAudioPlayer().getPlayingTrack() == null || guildPlayer.getAudioPlayer().isPaused()) {
                sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 465));
                return;
            }
            AudioTrack audioTrack = guildPlayer.getAudioPlayer().getPlayingTrack();
            guildPlayer.setLoopedTrack(audioTrack);
            sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 370));
        } else {
            guildPlayer.setLoopedTrack(null);
            sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 371));
        }
    }
}
