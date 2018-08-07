package me.purox.devi.commands.guild;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaitingResponseBuilder;
import net.dv8tion.jda.core.Permission;

import java.util.List;

public class PrefixCommandExecutor implements CommandExecutor {

    private final Devi devi;
    public PrefixCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        new WaitingResponseBuilder(devi, command)
                .setWaiterType(WaitingResponseBuilder.WaiterType.TEXT)
                .setSetting(GuildSettings.Settings.PREFIX)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 406))
                .build().handle();
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 248;
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
        return ModuleType.MANAGEMENT_COMMANDS;
    }
}
