package me.purox.devi.commandsold.music;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.music.GuildPlayer;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;

public class ShuffleCommandExecutor implements CommandExecutor {

    private Devi devi;

    public ShuffleCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        GuildPlayer guildPlayer = devi.getMusicManager().getGuildPlayer(command.getEvent().getGuild());

        if (guildPlayer.getQueue().isEmpty()) {
            sender.reply(Emote.ERROR + devi.getTranslation(command.getLanguage(), 139));
            return;
        }

        Member member = command.getEvent().getMember();
        if (!devi.getMusicManager().isDJorAlone(member, member.getVoiceState().getChannel(), member.getGuild())) {
            sender.reply(devi.getTranslation(command.getLanguage(), 454));
            return;
        }

        guildPlayer.shuffle();
        sender.reply(Emote.SUCCESS + " | " + devi.getTranslation(command.getLanguage(), 140));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 138;
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
