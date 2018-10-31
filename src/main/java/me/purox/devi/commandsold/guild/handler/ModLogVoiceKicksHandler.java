package me.purox.devi.commandsold.guild.handler;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaitingResponseBuilder;

public class ModLogVoiceKicksHandler {

    private Devi devi;
    public ModLogVoiceKicksHandler(Devi devi) {
        this.devi = devi;
    }

    public void handle(ICommand ICommand, CommandSender sender) {
        new WaitingResponseBuilder(devi, ICommand)
                .setExpectedInputText(devi.getTranslation(ICommand.getLanguage(), 571))
                .setWaiterType(WaitingResponseBuilder.WaiterType.BOOLEAN)
                .setSetting(GuildSettings.Settings.MOD_LOG_VOICEKICKS)
                .build().handle();
    }
}

