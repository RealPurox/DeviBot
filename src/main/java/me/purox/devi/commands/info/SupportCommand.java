package me.purox.devi.commands.info;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;

import java.awt.*;

public class SupportCommand extends ICommand {

    private Devi devi;

    public SupportCommand(Devi devi) {
        super("support");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.decode("#4775d1"));
        embed.setDescription(devi.getTranslation(command.getLanguage(), 489));
        sender.reply(new MessageBuilder()
                .setContent("https://discord.gg/6Ac4uqZ")
                .setEmbed(embed.build()).build());
    }
}
