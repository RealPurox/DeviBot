package me.purox.devi.commandsold.guild.custom;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.entities.Command;
import net.dv8tion.jda.core.Permission;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class AddCustomCommandExecutor implements CommandExecutor {

    private Devi devi;
    public AddCustomCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
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
            command.getDeviGuild().getCommandEntities().add(Devi.GSON.fromJson(cmd.toJson(), Command.class));
            sender.reply(devi.getTranslation(command.getLanguage(), 188, "`" + invoke + "`"));
        }
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 177;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("addcmd", "createcmd", "createcommand");
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

