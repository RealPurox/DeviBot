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
import net.dv8tion.jda.core.entities.Member;

import java.util.List;

public class SkipCommandExecutor implements CommandExecutor {

    private Devi devi;

    public SkipCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());

        Member member = command.getEvent().getMember();
        if (!devi.getMusicManager().isDJorAlone(member, member.getVoiceState().getChannel(), member.getGuild())) {
            sender.reply(devi.getTranslation(command.getLanguage(), 454));
            return;
        }

        if (guildPlayer.getAudioPlayer().isPaused()) {
            sender.reply(DeviEmote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 464));
            return;
        }

        if (guildPlayer.getAudioPlayer().getPlayingTrack() == null) {
            sender.reply(DeviEmote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 465));
            return;
        }
        int amount;
        try {
            amount = args.length == 0 ? 1 : Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            amount = 1;
        }
        if (amount < 1) amount = 1;

        int index = guildPlayer.getCurrentQueueIndex();
        int size = guildPlayer.getQueue().size();

        for (int i = 0; i != amount; i++) {
            if (index == -1 || index == size) {
                index = 0;
                continue;
            }
            index += 1;
        }


        guildPlayer.setCurrentQueueIndex(index);
        guildPlayer.getAudioPlayer().stopTrack();

        AudioInfo next = guildPlayer.getCurrent();
        sender.reply(DeviEmote.MUSIC.get() + " | " + devi.getTranslation(command.getLanguage(), 137, "**" + next.getAudioTrack().getInfo().title + "**"));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 131;
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
