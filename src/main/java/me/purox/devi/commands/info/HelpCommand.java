package me.purox.devi.commands.info;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.Language;
import me.purox.devi.utils.JavaUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand  implements Command {

    private Devi devi;

    public HelpCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = sender.isConsoleSender() ? null : devi.getDeviGuild(event.getGuild().getId());
        Language language = deviGuild == null ? Language.ENGLISH : Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild == null ? devi.getSettings().getDefaultPrefix() : deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        List<String> raw = new ArrayList<>(devi.getCommandHandler().getUnmodifiedCommands().keySet());
        List<List<String>> pages = JavaUtils.chopList(raw, 5);

        int page;
        try {
            page = args.length > 0 ? Integer.parseInt(args[0]) : 0;
        } catch (NumberFormatException e) {
            page = 1;
        }

        int total = pages.size();
        if (page > total) page = total;
        else if (page < 1 ) page = 1;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(34, 113, 126));
        builder.setAuthor(devi.getTranslation(language, 32, page, pages.size()));
        builder.setFooter(devi.getTranslation(language, 33, prefix + "help [page]"), null);

        for (String c : pages.get(page - 1)) {
            Command cmd = devi.getCommandHandler().getUnmodifiedCommands().get(c);
            builder.appendDescription("**" + prefix + c + "**\n");
            builder.appendDescription(" - " + (devi.getTranslation(language, 34, devi.getTranslation(language, cmd.getDescriptionTranslationID()))) + "\n");
            builder.appendDescription(" - " + (devi.getTranslation(language, 35, (cmd.getPermission() == null ? "N/A" : cmd.getPermission().name()))) + "\n");
            builder.appendDescription(" - " + (devi.getTranslation(language, 36, (cmd.getAliases() == null ? "N/A" :
                    "`" + cmd.getAliases().stream().collect(Collectors.joining("`, `")) + "`"))) + "\n\n");
        }

        sender.reply(builder.build());
    }


    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 38;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
