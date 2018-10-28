package me.purox.devi.commandsold.music;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
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
    public void execute(String[] args, ICommand command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());

        if (!devi.getMusicManager().isDJorAlone(command.getEvent().getMember(), command.getEvent().getGuild().getMember(sender).getVoiceState().getChannel(), command.getEvent().getGuild())) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 454));
            return;
        }

        if (!guildPlayer.getAudioPlayer().isPaused()) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 475));
            return;
        }

        guildPlayer.getAudioPlayer().setPaused(false);
        sender.reply(Emote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 476));

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
