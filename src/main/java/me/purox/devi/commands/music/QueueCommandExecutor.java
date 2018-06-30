package me.purox.devi.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.AudioInfo;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.List;

public class QueueCommandExecutor implements CommandExecutor {

    private Devi devi;

    public QueueCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());

        EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#36393E"));
        builder.setAuthor(command.getEvent().getGuild().getName() + " - Music Queue");
        builder.appendDescription(DeviEmote.MUSIC.get() + " __**Currently Playing**__ " + DeviEmote.MUSIC.get() + "\n\n");

        boolean displayNext = false;

        if (guildPlayer.getAudioPlayer().isPaused()) {
            builder.appendDescription(DeviEmote.ERROR.get() + " | The music player is currently paused!\n\n");
        } else if (guildPlayer.getAudioPlayer().getPlayingTrack() == null) {
            builder.appendDescription(DeviEmote.ERROR.get() + " | The music player is not playing any music right now!\n\n");
        } else {
            displayNext = true;
            AudioInfo currentInfo = guildPlayer.getCurrent();
            AudioTrack current = currentInfo.getAudioTrack();

            builder.appendDescription("[" + current.getInfo().title +"](" + current.getInfo().uri + ") - requested by **"
                    + currentInfo.getRequester().getName() + "#" + currentInfo.getRequester().getDiscriminator() + "**\n\n");
        }

        if (displayNext) {
            builder.appendDescription(":arrow_double_down: __**Up Next**__ :arrow_double_down:\n\n");

            int amount = 5;
            List<AudioInfo> audioInfos = guildPlayer.getNextSongs(amount);
            boolean isMore = audioInfos.size() >= amount;

            for (AudioInfo audioInfo : audioInfos) {
                AudioTrack current = audioInfo.getAudioTrack();
                builder.appendDescription("[" + current.getInfo().title +"](" + current.getInfo().uri + ") - requested by **"
                        + audioInfo.getRequester().getName() + "#" + audioInfo.getRequester().getDiscriminator() + "**\n\n");
            }

            if (isMore) {
                builder.appendDescription("[Click here](https://www.devibot.net/guild/" + command.getEvent().getGuild().getId() + "/queue) to display the rest of the queue");
            }
        }

        sender.reply(builder.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 117;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MUSIC;
    }
}
