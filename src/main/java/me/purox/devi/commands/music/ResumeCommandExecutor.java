package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class ResumeCommandExecutor implements CommandExecutor {

    private Devi devi;
    public ResumeCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (!devi.getMusicManager().getPlayer(command.getEvent().getGuild()).isPaused()) {
            sender.reply(devi.getTranslation(command.getLanguage(), 127));
            return;
        }

        devi.getMusicManager().getPlayer(command.getEvent().getGuild()).setPaused(false);
        sender.reply(devi.getTranslation(command.getLanguage(), 126));
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
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
