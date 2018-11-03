package me.purox.devi.commands.management;

import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.entities.Command;
import org.bson.Document;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class AddCommandCommand extends ICommand {

    private Devi devi;

    public AddCommandCommand(Devi devi) {
        super("addcommand", "addcmd", "createcmd", "createcommand");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        String[] args = command.getArgs();

        if (args.length < 2) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "addcommand <command> <response>`"));
            return;
        }

        String invoke = args[0];
        String response = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        AtomicBoolean doesCommandExist = new AtomicBoolean(false);
        command.getDeviGuild().getCommandEntities().forEach(cmd -> {
            if (cmd.getInvoke().equalsIgnoreCase(invoke)) {
                doesCommandExist.set(true);
            }
        });

        if (doesCommandExist.get()) {
            sender.reply(devi.getTranslation(command.getLanguage(), 187, "`" + invoke + "`", "`" + command.getPrefix() + "editcommand " + invoke + " <response>`"));
            return;
        }

        Document cmd = new Document();
        cmd.put("guild", command.getDeviGuild().getId());
        cmd.put("invoke", invoke);
        cmd.put("response", response);

        if (devi.getDatabaseManager().saveToDatabase("commands", cmd).wasAcknowledged()) {
            command.getDeviGuild().getCommandEntities().add(Devi.GSON.fromJson(cmd.toJson(), me.purox.devi.core.guild.entities.Command.class));
            sender.reply(devi.getTranslation(command.getLanguage(), 188, "`" + invoke + "`"));
        }

    }
}
