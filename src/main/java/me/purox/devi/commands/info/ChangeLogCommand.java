package me.purox.devi.commands.info;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public class ChangeLogCommand extends ICommand {

    private Devi devi;

    public ChangeLogCommand(Devi devi) {
        super("changelog", "changes", "updates", "changelogs");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.decode("#4775d1"));
        embed.setTitle(" ");
        embed.setDescription(devi.getTranslation(command.getLanguage(), 492));
        sender.reply(embed.build());
    }
}
