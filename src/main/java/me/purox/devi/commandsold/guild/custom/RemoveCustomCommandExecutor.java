package me.purox.devi.commandsold.guild.custom;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.entities.Command;
import net.dv8tion.jda.core.Permission;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class RemoveCustomCommandExecutor implements CommandExecutor {

    private Devi devi;

    public RemoveCustomCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        if (args.length == 0) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "removecommand <command>`"));
            return;
        }

        String invoke = args[0];

        AtomicBoolean doesCommandExist = new AtomicBoolean(false);
        AtomicReference<Command> oldCommand = new AtomicReference<>();

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
        return Permission.MANAGE_SERVER;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.MANAGEMENT_COMMANDS;
    }
}
