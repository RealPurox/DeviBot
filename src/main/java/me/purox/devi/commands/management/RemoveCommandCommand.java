package me.purox.devi.commands.management;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.entities.Command;
import org.bson.Document;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RemoveCommandCommand extends ICommand {

    private Devi devi;

    public RemoveCommandCommand(Devi devi) {
        super("removecommand", "removecom", "removecmd", "rmcommand", "rmcom", "deletecommand", "delcom", "delcommand");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length == 0) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "removecommand <command>`"));
            return;
        }

        String invoke = command.getArgs()[0];

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

        if (devi.getDatabaseManager().removeFromDatabase("commands", doc.getString("_id")).wasAcknowledged()) {
            command.getDeviGuild().getCommandEntities().remove(oldCommand.get());
            sender.reply(devi.getTranslation(command.getLanguage(), 196, "`" + invoke + "`"));
        }
    }
}
