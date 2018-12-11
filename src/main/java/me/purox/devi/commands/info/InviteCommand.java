package me.purox.devi.commands.info;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public class InviteCommand extends ICommand {

    private Devi devi;

    public InviteCommand(Devi devi) {
        super("invite");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(devi.getColor());
        embed.setAuthor(devi.getTranslation(command.getLanguage(), 622), null, command.getJDA().getSelfUser().getAvatarUrl());
        embed.setDescription(devi.getTranslation(command.getLanguage(), 623, "(https://discordapp.com/oauth2/authorize?client_id=354361427731152907&scope=bot)"));
        sender.reply(embed.build());
    }
}
