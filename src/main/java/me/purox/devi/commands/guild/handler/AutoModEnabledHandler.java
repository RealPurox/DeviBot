package me.purox.devi.commands.guild.handler;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaitingResponseBuilder;

public class AutoModEnabledHandler {

    private Devi devi;
    public AutoModEnabledHandler(Devi devi) {
        this.devi = devi;
    }

    public void handle(Command command, CommandSender sender) {
        new WaitingResponseBuilder(devi, command)
                .setExpectedInputText(devi.getTranslation(command. getLanguage(), 425))
                .setWaiterType(WaitingResponseBuilder.WaiterType.BOOLEAN)
                .setSetting(GuildSettings.Settings.AUTO_MOD_ENABLED)
                .build().handle();
    }
}
