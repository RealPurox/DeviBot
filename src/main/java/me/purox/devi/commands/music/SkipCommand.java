package me.purox.devi.commands.music;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.music.AudioInfo;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.entities.Member;

public class SkipCommand extends ICommand {

    private Devi devi;

    public SkipCommand(Devi devi) {
        super("skip");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getGuild());

        Member member = command.getMember();
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
            amount = command.getArgs().length == 0 ? 1 : Integer.parseInt(command.getArgs()[0]);
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
}
