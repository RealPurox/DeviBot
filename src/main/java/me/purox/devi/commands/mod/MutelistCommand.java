package me.purox.devi.commands.mod;

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
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.bson.Document;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MutelistCommand implements Command {

    private Devi devi;

    public MutelistCommand(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, MessageReceivedEvent event, CommandSender sender) {
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
        String prefix = deviGuild.getSettings().getStringValue(GuildSettings.Settings.PREFIX);

        Document document = deviGuild.getMuted();
        List<String> docs = new ArrayList<>(document.keySet());
        List<List<String>> mutes = JavaUtils.chopList(docs, 10);

        if (mutes.size() == 0) {
            MessageUtils.sendMessage(event.getChannel(), devi.getTranslation(language, 64));
            return;
        }

        int page;
        try {
            page = args.length > 0 ? Integer.parseInt(args[0]) : 0;
        } catch (NumberFormatException e) {
            page = 1;
        }

        int total = mutes.size();
        if (page > total) page = total;
        else if (page < 1) page = 1;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(34, 113, 126));
        builder.setAuthor(devi.getTranslation(language, 63, page, mutes.size()));
        builder.setFooter(devi.getTranslation(language, 33, prefix + "mutelist [page]"), null);
        builder.setDescription("This is a list of all muted members on this server. :mute:\n\n");

        for(String index : mutes.get(page -1 )) {
            Document doc = (Document) document.get(index);
            Member member = event.getGuild().getMemberById(index);
            builder.appendDescription("**" + (member == null ? index : member.getUser().getName() + "#" + member.getUser().getDiscriminator()) + ":**\n");
            builder.appendDescription(" - " + (devi.getTranslation(language, 47) + " " + (doc.getString("punisher") == null ? "N/A" : doc.getString("punisher"))) + "\n");
            builder.appendDescription(" - " + (devi.getTranslation (language, 48 ) + " " + (doc.getString("reason") == null ? "N/A" : doc.getString("reason"))) + "\n\n");
        }

        MessageUtils.sendMessage(event.getChannel(), builder.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 42;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return Permission.VOICE_MUTE_OTHERS;
    }
}
