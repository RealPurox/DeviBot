package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;

import java.util.Arrays;
import java.util.List;

public class RemoveStreamCommandExecutor implements CommandExecutor {

    private Devi devi;
    public RemoveStreamCommandExecutor(Devi devi) {
        this.devi = devi;
    }


    @Override
    public void execute(String[] args, Command command, CommandSender sender) {

    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 201;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("rmstream", "removetwitch", "rmtwitch");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
