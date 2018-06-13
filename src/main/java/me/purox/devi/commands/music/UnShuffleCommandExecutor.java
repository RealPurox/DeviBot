package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class UnShuffleCommandExecutor implements CommandExecutor {

    private Devi devi;
    public UnShuffleCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (devi.getMusicManager().getManager(command.getEvent().getGuild()).getQueue().isEmpty()) {
            sender.reply(devi.getTranslation(command.getLanguage(), 139));
            return;
        }

        if (!command.getEvent().getMember().getVoiceState().inVoiceChannel()) {
            sender.reply(devi.getTranslation(command.getLanguage(), 100));
            return;
        }

        devi.getMusicManager().getManager(command.getEvent().getGuild()).unShuffleQueue();
        sender.reply(devi.getTranslation(command.getLanguage(), 157));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 156;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MUSIC;
    }
}
