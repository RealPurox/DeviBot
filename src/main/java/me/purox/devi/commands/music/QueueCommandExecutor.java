package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.AudioInfo;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class QueueCommandExecutor implements CommandExecutor {

    private Devi devi;

    public QueueCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());
        List<AudioInfo> queue = guildPlayer.getQueue();

        StringBuilder sb = new StringBuilder();

        int i = 1;
        for (AudioInfo audioInfo : queue) {
            int next = i++;
            System.out.println(next);
            System.out.println(guildPlayer.getCurrentQueueIndex());
            if (next == guildPlayer.getCurrentQueueIndex() + 1)  sb.append("->>> ");
            sb.append(next).append(") `").append(audioInfo.getAudioTrack().getInfo().title).append("` - requested by **").append(audioInfo.getRequester().getName()).append("#").append(audioInfo.getRequester().getDiscriminator()).append("**\n");
        }

        sender.reply(sb.toString());
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
