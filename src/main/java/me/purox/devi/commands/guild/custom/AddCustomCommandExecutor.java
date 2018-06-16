package me.purox.devi.commands.guild.custom;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
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
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 2) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "addcommand <command> <response>`"));
            return;
        }

        String invoke = args[0];
        String response = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        AtomicBoolean doesCommandExist = new AtomicBoolean(false);
        command.getDeviGuild().getCommands().forEach(document -> {
            if (document.getString("invoke").equalsIgnoreCase(invoke)) {
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
            command.getDeviGuild().getCommands().add(cmd);
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
        return ModuleType.CUSTOM_COMMANDS;
    }
}

