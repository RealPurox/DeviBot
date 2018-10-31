package me.purox.devi.commands.admin;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;

public class ReloadCommand extends ICommand {

    private Devi devi;

    public ReloadCommand(Devi devi) {
        super("reload");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (!devi.getAdmins().contains(sender.getId())) return;
        devi.loadTranslations();
        sender.reply("Translations reloaded " + devi.getAnimatedEmotes().PartyParrotEmote().getAsMention());
    }
}
