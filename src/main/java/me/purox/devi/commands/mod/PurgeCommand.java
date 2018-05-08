package me.purox.devi.commands.mod;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class PurgeCommand implements Command {

    private Devi devi;
    public PurgeCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String command, String[] args, MessageReceivedEvent event) {

    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 0;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
