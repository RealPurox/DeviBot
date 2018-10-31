package me.purox.devi.commands.admin;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;

public class TestCommand extends ICommand {

    private Devi devi;

    public TestCommand(Devi devi) {
        super("test", "testing", "tester");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        sender.reply("New command tracker works perfect.");
    }
}
