package me.purox.devi.commands.music;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.music.GuildPlayer;

public class PauseCommand extends ICommand {

    private Devi devi;

    public PauseCommand(Devi devi) {
        super("pause", "stop");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getGuild());

        if (!devi.getMusicManager().isDJorAlone(command.getMember(), command.getGuild().getMember(sender).getVoiceState().getChannel(), command.getGuild())) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 454));
            return;
        }

        if (guildPlayer.getAudioPlayer().isPaused()) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 477, "`" + command.getPrefix() + "unpause`"));
            return;
        }

        guildPlayer.getAudioPlayer().setPaused(true);
        sender.reply(Emote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 478));
    }

}
