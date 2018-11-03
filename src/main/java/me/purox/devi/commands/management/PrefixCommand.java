package me.purox.devi.commands.management;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaitingResponseBuilder;

public class PrefixCommand extends ICommand {

    private Devi devi;

    public PrefixCommand(Devi devi) {
        super("prefix");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        new WaitingResponseBuilder(devi, command)
                .setWaiterType(WaitingResponseBuilder.WaiterType.TEXT)
                .setSetting(GuildSettings.Settings.PREFIX)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 406))
                .build().handle();
    }
}
