package me.purox.devi.commandsold.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class LoopCommandExecutor implements CommandExecutor {

    private Devi devi;

    public LoopCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());

        if (!devi.getMusicManager().isDJorAlone(command.getEvent().getMember(), command.getEvent().getGuild().getMember(sender).getVoiceState().getChannel(), command.getEvent().getGuild())) {
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

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 369;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_CHANNEL;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MUSIC;
    }
}
