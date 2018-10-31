package me.purox.devi.commands.fun;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;

import java.util.Random;

public class FlipCoinCommand extends ICommand {

    private Devi devi;
    private Random random;

    public FlipCoinCommand(Devi devi) {
        super("flipcoin", "coinflip");
        this.devi = devi;
        this.random = new Random();
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        sender.reply(devi.getTranslation(command.getLanguage(), random.nextInt(2) == 0 ? 402 : 403));
    }
}
