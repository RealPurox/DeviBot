package me.purox.devi.commands.dev;

import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class ReloadCommandExecutor implements CommandExecutor {

    private Devi devi;
    public ReloadCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        if (event != null && !devi.getAdmins().contains(sender.getId()) && !event.getAuthor().getId().equals("161494492422078464") && !sender.isConsoleSender()) return;
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
