package me.purox.devi.commands.management;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import org.bson.Document;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class EditCommandCommand extends ICommand {

    private Devi devi;

    public EditCommandCommand(Devi devi) {
        super("editcommand", "editcom", "commandedit");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String[] args = command.getArgs();

        if (args.length < 2) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "editcommand <command> <response>`"));
            return;
        }

        String invoke = args[0];
        String response = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        AtomicBoolean doesCommandExist = new AtomicBoolean(false);
        AtomicReference<me.purox.devi.core.guild.entities.Command> oldCommand = new AtomicReference<>();

        command.getDeviGuild().getCommandEntities().forEach(cmd -> {
            if (cmd.getInvoke().equalsIgnoreCase(invoke)) {
                oldCommand.set(cmd);
                doesCommandExist.set(true);
            }
        });

        if (!doesCommandExist.get()) {
            sender.reply(devi.getTranslation(command.getLanguage(), 195, "`" + invoke + "`"));
            return;
        }

        Document doc = new Document().append("_id", oldCommand.get().get_id()).append("guild", oldCommand.get().getGuild()).append("invoke", oldCommand.get().getInvoke()).append("response", oldCommand.get().getResponse());

        if (devi.getDatabaseManager().saveToDatabase("commands", doc, doc.getString("_id")).wasAcknowledged()) {
            command.getDeviGuild().getCommandEntities().remove(oldCommand.get());
            command.getDeviGuild().getCommandEntities().add(Devi.GSON.fromJson(doc.toJson(), me.purox.devi.core.guild.entities.Command.class));
            sender.reply(devi.getTranslation(command.getLanguage(), 197, "`" + invoke + "`"));
        }
    }
}
