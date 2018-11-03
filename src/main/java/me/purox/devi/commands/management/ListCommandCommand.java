package me.purox.devi.commands.management;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.entities.Command;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.List;

public class ListCommandCommand extends ICommand {

    private Devi devi;

    public ListCommandCommand(Devi devi) {
        super("listcommand", "listcommands", "listcmd", "cmdlist", "commandlist", "commandslist");
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        List<me.purox.devi.core.guild.entities.Command> commandEntities = command.getDeviGuild().getCommandEntities();
        List<List<me.purox.devi.core.guild.entities.Command>> pages = JavaUtils.chopList(commandEntities, 5);

        int page;
        try {
            page = command.getArgs().length > 0 ? Integer.parseInt(command.getArgs()[0]) : 0;
        } catch (NumberFormatException e) {
            page = 1;
        }

        int total = pages.size();
        if (page > total) page = total;
        else if (page < 1 ) page = 1;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(100, 248, 255));
        builder.setAuthor(devi.getTranslation(command.getLanguage(), 193, page, pages.size()));
        builder.setFooter(devi.getTranslation(command.getLanguage(), 33, command.getPrefix() + "commandlist [page]"), null);


        if (!pages.isEmpty()) {
            for (me.purox.devi.core.guild.entities.Command cmd : pages.get(page - 1)) {
                builder.addField("\u27A4 " + cmd.getInvoke(), cmd.getResponse(), false);
            }
        }

        sender.reply(builder.build());

    }
}
