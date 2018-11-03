package me.purox.devi.commands.info;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;

import java.util.concurrent.TimeUnit;

public class PingCommand extends ICommand {

    private Devi devi;

    public PingCommand(Devi devi) {
        super("ping");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        command.getChannel().sendMessage(devi.getTranslation(command.getLanguage(), 543))
                .queue(message -> message.editMessage(devi.getTranslation(command.getLanguage(), 543) + " `" + command.getJDA().getPing() + " ms`")
                        .queueAfter(1, TimeUnit.SECONDS));
    }
}
