package me.purox.devi.commandsold.guild.handler;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.waiter.WaitingResponseBuilder;

public class AutoModEmojiHandler {

    private Devi devi;
    public AutoModEmojiHandler(Devi devi) {
        this.devi = devi;
    }

    public void handle(ICommand command, CommandSender sender) {
        new WaitingResponseBuilder(devi, command)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 424))
                .setWaiterType(WaitingResponseBuilder.WaiterType.BOOLEAN)
                .setSetting(GuildSettings.Settings.AUTO_MOD_ANTI_EMOJI)
                .build().handle();
    }
}
