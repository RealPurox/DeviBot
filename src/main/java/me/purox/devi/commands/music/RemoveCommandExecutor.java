package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.AudioInfo;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class RemoveCommandExecutor implements CommandExecutor {

    private Devi devi;

    public RemoveCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());

        if (!command.getEvent().getGuild().getSelfMember().getVoiceState().inVoiceChannel() || guildPlayer.getQueue().size() == 0) {
            sender.reply(DeviEmote.ERROR.get() + " | There are no songs in the queue");
            return;
        }

        if (!devi.getMusicManager().isDJorAlone(command.getEvent().getMember(), command.getEvent().getGuild().getSelfMember().getVoiceState().getChannel(), command.getEvent().getGuild())) {
            sender.reply(DeviEmote.ERROR.get() + " | You don't have permission to remove songs from the queue");
            return;
        }

        if (args.length == 0) {
            sender.reply(DeviEmote.ERROR.get() + " | Please specify the song ID you want to remove. Song ID's can be found in `" + command.getPrefix() + "queue`. " +
                    "For Example, `" + command.getPrefix() + "remove 1` will remove \"" + guildPlayer.getAudioInfoById(1).getAudioTrack().getInfo().title + "\"");
            return;
        }

        int remove;
        try {
            remove = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.reply(DeviEmote.ERROR.get() + " | Please enter the queue position of the song you want to be removed from the queue");
            return;
        }

        if (remove < 1 || remove > guildPlayer.getQueue().size()) {
            sender.reply(DeviEmote.ERROR.get() + " | There is no song at position " + remove + " in the queue!");
            return;
        }

        AudioInfo audioInfo = guildPlayer.getAudioInfoById(remove);
        if (audioInfo == null) {
            sender.reply(DeviEmote.ERROR.get() + " | There is no song at position " + remove + " in the queue!");
            return;
        }

        guildPlayer.getQueue().remove(audioInfo);
        if (remove == guildPlayer.getCurrentQueueIndex() + 1) {
            guildPlayer.getAudioPlayer().stopTrack();
        }
        sender.reply(DeviEmote.SUCCESS.get() + " | The song \"" + audioInfo.getAudioTrack().getInfo().title + "\" has been removed from the queue");
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 460;
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
