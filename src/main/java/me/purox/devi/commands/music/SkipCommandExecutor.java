package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
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
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 464));
            return;
        }

        if (guildPlayer.getAudioPlayer().getPlayingTrack() == null) {
            sender.reply(Emote.ERROR + " | " + devi.getTranslation(command.getLanguage(), 465));
            return;
        }
        int amount;
        try {
            amount = args.length == 0 ? 1 : Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            amount = 1;
        }
        if (amount < 1) amount = 1;

        for (int i = 0; i < amount - 1; i++) {
            guildPlayer.getQueue().remove();
        }

        guildPlayer.getAudioPlayer().stopTrack();

        if (guildPlayer.getQueue().isEmpty()) {
            sender.reply(Emote.INFO + " | " + devi.getTranslation(command.getLanguage(), 586));
            guildPlayer.destroy(true);
        } else {
            AudioInfo next = guildPlayer.getCurrent();
            sender.reply(Emote.MUSIC.get() + " | " + devi.getTranslation(command.getLanguage(), 137, "**" + next.getAudioTrack().getInfo().title + "**"));
        }
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
