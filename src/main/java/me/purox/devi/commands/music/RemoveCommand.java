package me.purox.devi.commands.music;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.music.AudioInfo;
import me.purox.devi.music.GuildPlayer;

public class RemoveCommand extends ICommand {

    private Devi devi;

    public RemoveCommand(Devi devi) {
        super("remove");
        this.devi = devi;
    }


    @Override
    public void execute(CommandSender sender, Command command) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getGuild());

        if (!command.getGuild().getSelfMember().getVoiceState().inVoiceChannel() || guildPlayer.getQueue().size() == 0) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 469));
            return;
        }

        if (!devi.getMusicManager().isDJorAlone(command.getMember(), command.getGuild().getSelfMember().getVoiceState().getChannel(), command.getGuild())) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 470));
            return;
        }

        if (command.getArgs().length == 0) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 471, "`" + command.getPrefix() + "queue`",
                    "`" + command.getPrefix() + "remove 1`", "\"" + guildPlayer.getAudioInfoById(1).getAudioTrack().getInfo().title + "\""));
            return;
        }

        int remove;
        try {
            remove = Integer.parseInt(command.getArgs()[0]);
        } catch (NumberFormatException e) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 472));
            return;
        }

        if (remove < 1 || remove > guildPlayer.getQueue().size()) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 473, remove));
            return;
        }

        AudioInfo audioInfo = guildPlayer.getAudioInfoById(remove);
        if (audioInfo == null) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 473, remove));
            return;
        }

        guildPlayer.getQueue().remove(audioInfo);
        if (remove == 1) {
            guildPlayer.getAudioPlayer().stopTrack();
        }
        sender.reply(Emote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 474, "\"" + audioInfo.getAudioTrack().getInfo().title + "\""));
    }
}
