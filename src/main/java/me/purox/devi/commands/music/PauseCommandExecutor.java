package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;

import java.util.Collections;
import java.util.List;

public class PauseCommandExecutor implements CommandExecutor {
    
    private Devi devi;
    public PauseCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (devi.getMusicManager().getPlayer(command.getEvent().getGuild()).isPaused()) {
            sender.reply(devi.getTranslation(command.getLanguage(), 123));
            return;
        }

        devi.getMusicManager().getPlayer(command.getEvent().getGuild()).setPaused(true);
        sender.reply(devi.getTranslation(command.getLanguage(), 124, "`" + command.getPrefix() + "resume`"));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 122;
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("stop");
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
