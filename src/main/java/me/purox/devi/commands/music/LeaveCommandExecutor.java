package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;

import java.util.List;

public class LeaveCommandExecutor implements CommandExecutor {

    private Devi devi;
    public LeaveCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!devi.getMusicManager().getManager(command.getEvent().getGuild()).getAudioPlayer().isPaused() && !devi.getMusicManager().isIdle(command.getEvent().getGuild())){
            sender.reply(devi.getTranslation(command.getLanguage(), 112, "`" + command.getPrefix() + "stop`"));
            return;
        }

        if (command.getEvent().getGuild().getAudioManager().getConnectionStatus() != ConnectionStatus.CONNECTED) {
            sender.reply(devi.getTranslation(command.getLanguage(), 113));
            return;
        }

        command.getEvent().getGuild().getAudioManager().closeAudioConnection();
        devi.getMusicManager().getManager(command.getEvent().getGuild()).clearQueue();
        devi.getMusicManager().getAudioPlayers().remove(command.getEvent().getGuild());
        sender.reply(devi.getTranslation(command.getLanguage(), 114));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 143;
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
