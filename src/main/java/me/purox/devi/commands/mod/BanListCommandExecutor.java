package me.purox.devi.commands.mod;

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
import java.util.ArrayList;
import java.util.List;

public class BanListCommandExecutor implements CommandExecutor {

    private Devi devi;

    public BanListCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        Document document = command.getDeviGuild().getBanned();
        List<String> docs = new ArrayList<>(document.keySet());
        List<List<String>> bans = JavaUtils.chopList(docs, 5);

        if (bans.size() == 0) {
            sender.reply(devi.getTranslation(command.getLanguage(), 65));
            return;
        }

        int page;
        try {
            page = args.length > 0 ? Integer.parseInt(args[0]) : 0;
        } catch (NumberFormatException e) {
            page = 1;
        }

        int total = bans.size();
        if (page > total) page = total;
        else if (page < 1) page = 1;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(34, 113, 126));
        builder.setAuthor(devi.getTranslation(command.getLanguage(), 63, page, bans.size()));
        builder.setFooter(devi.getTranslation(command.getLanguage(), 33, command.getPrefix() + "banlist [page]"), null);
        builder.setDescription("This is a list of all users that are banned from this server. :no_entry:\n\n");

        for(String index : bans.get(page -1 )) {
            Document doc = (Document) document.get(index);
            builder.appendDescription("**" + index + ":**\n");
            builder.appendDescription(" - " + (devi.getTranslation(command.getLanguage(), 66) + " " + (doc.getString("user") == null ? "N/A" : doc.getString("user"))) + "\n");
            builder.appendDescription(" - " + (devi.getTranslation(command.getLanguage(), 47) + " " + (doc.getString("punisher") == null ? "N/A" : doc.getString("punisher"))) + "\n");
            builder.appendDescription(" - " + (devi.getTranslation (command.getLanguage(), 48 ) + " " + (doc.getString("reason") == null ? "N/A" : doc.getString("reason"))) + "\n");
        }

        sender.reply(builder.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 40;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.BAN_MEMBERS;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MODERATION;
    }
}
