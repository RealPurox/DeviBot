package me.purox.devi.commands.guild.custom;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.bson.Document;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListCustomCommandExecutor implements CommandExecutor {

    private Devi devi;

    public ListCustomCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        List<Document> commands = command.getDeviGuild().getCommands();
        List<List<Document>> pages = JavaUtils.chopList(commands, 5);

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
            for (Document doc : pages.get(page - 1)) {
                builder.addField("\u27A4 " + doc.getString("invoke"), doc.getString("response"), false);
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
        return ModuleType.CUSTOM_COMMANDS;
    }
}
