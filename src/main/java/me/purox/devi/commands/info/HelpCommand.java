package me.purox.devi.commands.info;

import com.sun.org.apache.xpath.internal.operations.Mod;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.commands.ICommand;
import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.entities.ModuleType;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HelpCommand extends ICommand {

    private Devi devi;

    public HelpCommand(Devi devi) {
        super("help");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        HashMap<String, ICommand> commands = devi.getCommandHandler().getUnmodifiedCommands();
        if (command.getArgs().length == 0 ) {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setAuthor(devi.getTranslation(command.getLanguage(), 388), "https://www.devibot.net/wiki");
            builder.setColor(Color.decode("#7289da"));

            builder.appendDescription("\n");
            builder.appendDescription(devi.getTranslation(command.getLanguage(), 389, "`" + command.getPrefix() + "help <command>`") + " ");
            builder.appendDescription(devi.getTranslation(command.getLanguage(), 390) + " `" + command.getPrefix() + "help settings`\n\n");
            //builder.appendDescription("Use `" + command.getPrefix() + "modulehelp <module>` to get information about a specific module.\n");
            //builder.appendDescription("Example: `" + command.getPrefix() + "modulehelp music`");

            for (ModuleType moduleType : ModuleType.values()) {
                if (moduleType == ModuleType.DEV) continue;
                if (devi.getDisabledModules().contains(moduleType))
                    builder.addField(moduleType.getName(), devi.getTranslation(command.getLanguage(), 391), false);
                else if (commands.keySet().stream().anyMatch(invoke -> commands.get(invoke).getModuleType() == moduleType))
                    builder.addField(moduleType.getName(), "```asciidoc\n# " + commands.keySet().stream()
                            .filter(invoke -> commands.get(invoke).getModuleType() == moduleType)
                            .collect(Collectors.joining(", ")) + "```", false);
            }

            sender.reply(builder.build());
            return;
        }

        String invoke = command.getArgs()[0];

        if (!commands.containsKey(invoke)) {
            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 392, "`" + invoke + "`"));
            return;
        }

        char capital = Character.toUpperCase(invoke.charAt(0));
        String capitalInvoke = capital + invoke.toLowerCase().substring(1);


        ICommand cmd = commands.get(invoke);
        StringBuilder builder = new StringBuilder();

        builder.append("__**").append(capitalInvoke).append(" - ").append(devi.getTranslation(command.getLanguage(), 393)).append("**__\n\n");

        if (cmd.getPermission() != null)
            builder.append("`-` ").append(devi.getTranslation(command.getLanguage(), 394, cmd.getPermission().getName())).append("\n\n");

        if (cmd.isGuildOnly()) {
            builder.append("`-` ").append(devi.getTranslation(command.getLanguage(), 395)).append("\n\n");
        }
        if(cmd.getModuleType() == ModuleType.NSFW_COMMANDS) {
            builder.append("`-` ").append(devi.getTranslation(command.getLanguage(), 569)).append("\n\n");
        }

        builder.append("**").append(devi.getTranslation(command.getLanguage(), 396)).append(":** ").append(cmd.getModuleType().getName()).append("\n\n");
        builder.append("**").append(devi.getTranslation(command.getLanguage(), 397)).append(":** ").append(devi.getTranslation(command.getLanguage(), cmd.getDescriptionId())).append("\n\n");

        if (cmd.getAliases() == null)
            builder.append("**").append(devi.getTranslation(command.getLanguage(), 398)).append(":** ").append(devi.getTranslation(command.getLanguage(), 399));
        else builder.append("**").append(devi.getTranslation(command.getLanguage(), 398)).append(":** ").append("`").append(command.getPrefix()).append(cmd.getAliases().stream().collect(Collectors.joining("`, `" + command.getPrefix()))).append("`").append("\n\n");



        sender.reply(builder.toString());
    }
}
