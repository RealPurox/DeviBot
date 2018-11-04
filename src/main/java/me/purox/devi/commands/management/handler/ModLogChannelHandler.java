package me.purox.devi.commands.management.handler;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaitingResponseBuilder;

public class ModLogChannelHandler {

    private Devi devi;
    public ModLogChannelHandler(Devi devi) {
        this.devi = devi;
    }

    public void handle(ICommand.Command command) {
        new WaitingResponseBuilder(devi, command)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 327))
                .setWaiterType(WaitingResponseBuilder.WaiterType.CHANNEL)
                .setSetting(GuildSettings.Settings.MOD_LOG_CHANNEL)
                .build().handle();
    }
}