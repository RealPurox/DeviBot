package me.purox.devi.commands.management.handler;

import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaitingResponseBuilder;

public class ModLogVoiceKicksHandler {

    private Devi devi;
    public ModLogVoiceKicksHandler(Devi devi) {
        this.devi = devi;
    }

    public void handle(ICommand.Command command) {
        new WaitingResponseBuilder(devi, command)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 571))
                .setWaiterType(WaitingResponseBuilder.WaiterType.BOOLEAN)
                .setSetting(GuildSettings.Settings.MOD_LOG_VOICEKICKS)
                .build().handle();
    }
}

