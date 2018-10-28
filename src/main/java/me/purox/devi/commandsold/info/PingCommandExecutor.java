package me.purox.devi.commandsold.info;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PingCommandExecutor implements CommandExecutor {

    private Devi devi;

    public PingCommandExecutor(Devi devi){
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        command.getEvent().getChannel().sendMessage(devi.getTranslation(command.getLanguage(), 543)).queue(message -> message.editMessage(devi.getTranslation(command.getLanguage(), 543) + " `" + command.getEvent().getJDA().getPing() + " ms`").queueAfter(1, TimeUnit.SECONDS));

    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 543;
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
        return ModuleType.INFO_COMMANDS;
    }
}
