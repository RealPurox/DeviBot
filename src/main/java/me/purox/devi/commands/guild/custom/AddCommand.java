package me.purox.devi.commands.guild.custom;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class AddCommand implements Command {

    private Devi devi;
    public AddCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        if (args.length < 2) {
            sender.reply(devi.getTranslation(language, 12, "`" + prefix + "addcommand <command> <response>`"));
            return;
        }

        String invoke = args[0];
        String response = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));

        AtomicBoolean doesCommandExist = new AtomicBoolean(false);
        deviGuild.getCommands().forEach(document -> {
            if (document.getString("invoke").equalsIgnoreCase(invoke)) {
                doesCommandExist.set(true);
            }
        });

        if (doesCommandExist.get()) {
            sender.reply(devi.getTranslation(language, 187, "`" + invoke + "`", "`" + prefix + "editcommand " + invoke + " <response>`"));
            return;
        }

        Document command = new Document();
        command.put("guild", deviGuild.getId());
        command.put("invoke", invoke);
        command.put("response", response);

        deviGuild.getCommands().add(command);
        if (devi.getDatabaseManager().saveToDatabase("commands", command).wasAcknowledged()) {
            sender.reply(devi.getTranslation(language, 188, "`" + invoke + "`"));
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
}
