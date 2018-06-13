package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommandExecutor implements CommandExecutor {

    private Devi devi;

    public HelpCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        List<String> raw = new ArrayList<>(devi.getCommandHandler().getUnmodifiedCommands().keySet());
        List<List<String>> pages = JavaUtils.chopList(raw, 5);

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
        builder.setColor(new Color(34, 113, 126));
        builder.setAuthor(devi.getTranslation(command.getLanguage(), 32, page, pages.size()));
        builder.setFooter(devi.getTranslation(command.getLanguage(), 33, command.getPrefix() + "help [page]"), null);

        for (String c : pages.get(page - 1)) {
            CommandExecutor cmd = devi.getCommandHandler().getUnmodifiedCommands().get(c);
            builder.appendDescription("**" + command.getPrefix() + c + "**\n");
            builder.appendDescription(" - " + (devi.getTranslation(command.getLanguage(), 34, devi.getTranslation(command.getLanguage(), cmd.getDescriptionTranslationID()))) + "\n");
            builder.appendDescription(" - " + (devi.getTranslation(command.getLanguage(), 35, (cmd.getPermission() == null ? "N/A" : cmd.getPermission().name()))) + "\n");
            builder.appendDescription(" - " + (devi.getTranslation(command.getLanguage(), 36, (cmd.getAliases() == null ? "N/A" :
                    "`" + cmd.getAliases().stream().collect(Collectors.joining("`, `")) + "`"))) + "\n\n");
        }

        sender.reply(builder.build());
    }


    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 38;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return null;
    }
}

