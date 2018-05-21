package me.purox.devi.commands.music;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class VolumeCommandExecutor implements CommandExecutor {

    private Devi devi;
    public VolumeCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 1) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "volume <1 - 100>`"));
            return;
        }

        int volume;
        try {
            volume = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            volume = -1;
        }

        if (volume < 1 || volume > 100) {
            sender.reply(devi.getTranslation(command.getLanguage(), 191));
            return;
        }

        devi.getMusicManager().getPlayer(command.getEvent().getGuild()).setVolume(volume);
        sender.reply(devi.getTranslation(command.getLanguage(), 190, volume));
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 189;
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
