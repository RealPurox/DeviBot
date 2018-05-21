package me.purox.devi.commands.guild.custom;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.Permission;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class RemoveCustomCommandExecutor implements CommandExecutor {

    private Devi devi;

    public RemoveCustomCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length == 0) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "removecommand <command>`"));
            return;
        }

        String invoke = args[0];

        AtomicBoolean doesCommandExist = new AtomicBoolean(false);
        AtomicReference<Document> atomicDoc = new AtomicReference<>();

        command.getDeviGuild().getCommands().forEach(document -> {
            if (document.getString("invoke").equalsIgnoreCase(invoke)) {
                atomicDoc.set(document);
                doesCommandExist.set(true);
            }
        });

        if (!doesCommandExist.get()) {
            sender.reply(devi.getTranslation(command.getLanguage(), 195, "`" + invoke + "`"));
            return;
        }

        Document doc = atomicDoc.get();

        if (devi.getDatabaseManager().removeFromDatabase("commands", doc.getString("_id")).wasAcknowledged()) {
            command.getDeviGuild().getCommands().remove(doc);
            sender.reply(devi.getTranslation(command.getLanguage(), 196, "`" + invoke + "`"));
        }
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 194;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("rmcom", "rmcommand");
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
