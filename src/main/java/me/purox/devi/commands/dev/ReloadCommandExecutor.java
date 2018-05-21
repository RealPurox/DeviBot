package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class ReloadCommandExecutor implements CommandExecutor {

    private Devi devi;
    public ReloadCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (command.getEvent() != null && !devi.getAdmins().contains(sender.getId()) && !sender.getId().equals("161494492422078464") && !sender.isConsoleCommandSender()) return;
        devi.loadTranslations();
        sender.reply("Translations reloaded");
    }

    @Override
    public boolean guildOnly() {
        return false;
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
