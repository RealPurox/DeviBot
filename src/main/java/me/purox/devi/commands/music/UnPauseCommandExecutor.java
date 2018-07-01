package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.Permission;

import java.util.Collections;
import java.util.List;

public class UnPauseCommandExecutor implements CommandExecutor {

    private Devi devi;

    public UnPauseCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());

        if (!devi.getMusicManager().isDJorAlone(command.getEvent().getMember(), command.getEvent().getGuild().getMember(sender).getVoiceState().getChannel(), command.getEvent().getGuild())) {
            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 454));
            return;
        }

        if (!guildPlayer.getAudioPlayer().isPaused()) {
            sender.reply(DeviEmote.ERROR.get() + " | The music player isn't paused.");
            return;
        }

        guildPlayer.getAudioPlayer().setPaused(false);
        sender.reply(DeviEmote.SUCCESS.get() + " | The music player has been resumed!");

    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 125;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("resume");
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
