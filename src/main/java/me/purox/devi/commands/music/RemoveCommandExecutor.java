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
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 469));
            return;
        }

        if (!devi.getMusicManager().isDJorAlone(command.getEvent().getMember(), command.getEvent().getGuild().getSelfMember().getVoiceState().getChannel(), command.getEvent().getGuild())) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 470));
            return;
        }

        if (args.length == 0) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 471, "`" + command.getPrefix() + "queue`",
                    "`" + command.getPrefix() + "remove 1`", "\"" + guildPlayer.getAudioInfoById(1).getAudioTrack().getInfo().title + "\""));
            return;
        }

        int remove;
        try {
            remove = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 472));
            return;
        }

        if (remove < 1 || remove > guildPlayer.getQueue().size()) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), remove));
            return;
        }

        AudioInfo audioInfo = guildPlayer.getAudioInfoById(remove);
        if (audioInfo == null) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), remove));
            return;
        }

        guildPlayer.getQueue().remove(audioInfo);
        if (remove == guildPlayer.getCurrentQueueIndex() + 1) {
            guildPlayer.getAudioPlayer().stopTrack();
        }
        sender.reply(Emote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 474, "\"" + audioInfo.getAudioTrack().getInfo().title + "\""));
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
