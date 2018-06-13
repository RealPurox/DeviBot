package me.purox.devi.commands.guild.custom;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import net.dv8tion.jda.core.Permission;
import org.bson.Document;

import javax.print.Doc;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class EditCustomCommandExecutor implements CommandExecutor {

    private Devi devi;
    public EditCustomCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 2) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "editcommand <command> <response>`"));
            return;
        }

        String invoke = args[0];
        String response = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

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

        Document oldDocument = atomicDoc.get();
        Document doc = new Document(oldDocument);
        doc.put("response", response);

        if (devi.getDatabaseManager().saveToDatabase("commands", doc, doc.getString("_id")).wasAcknowledged()) {
            command.getDeviGuild().getCommands().remove(oldDocument);
            command.getDeviGuild().getCommands().add(doc);
            sender.reply(devi.getTranslation(command.getLanguage(), 197, "`" + invoke + "`"));
        }

    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 228;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("editcom", "commandedit");
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
