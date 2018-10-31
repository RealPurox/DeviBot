package me.purox.devi.commandsold.guild.custom;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.entities.Command;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class ListCustomCommandExecutor implements CommandExecutor {

    private Devi devi;

    public ListCustomCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        List<Command> commandEntities = command.getDeviGuild().getCommandEntities();
        List<List<Command>> pages = JavaUtils.chopList(commandEntities, 5);

        int page;
        try {
            page = args.length > 0 ? Integer.parseInt(args[0]) : 0;
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
            for (Command cmd : pages.get(page - 1)) {
                builder.addField("\u27A4 " + cmd.getInvoke(), cmd.getResponse(), false);
            }
        }

        sender.reply(builder.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 192;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("cmdlist", "listcmd", "listcommands", "listcommand");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MANAGEMENT_COMMANDS;
    }
}
