package me.purox.devi.commandsold.guild;

import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.entities.Stream;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.bson.Document;
import org.json.JSONObject;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListStreamCommandExecutor implements CommandExecutor {

    private Devi devi;
    public ListStreamCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        List<Stream> rawStreams = command.getDeviGuild().getStreams();
        List<List<Stream>> rawStreamDocumentPages = JavaUtils.chopList(rawStreams, 5);

        int page;
        try {
            page = args.length > 0 ? Integer.parseInt(args[0]) : 0;
        } catch (NumberFormatException e) {
            page = 1;
        }

        int total = rawStreamDocumentPages.size();
        if (page > total) page = total;
        else if (page < 1 ) page = 1;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(new Color(100, 65, 164));
        builder.setAuthor(devi.getTranslation(command.getLanguage(), 213, page, total), null, "https://www.twitch.tv/p/assets/uploads/glitch_474x356.png");
        builder.setFooter(devi.getTranslation(command.getLanguage(), 33, command.getPrefix() + "streamlist [page]"), null);

        for (Stream stream : rawStreamDocumentPages.get(page - 1)) {
            String raw = devi.getRedisSender().hget("streams#1", stream.getStream());
            JSONObject data;

            if (raw == null) data = null;
            else data = new JSONObject(raw);
            if (data == null) continue;
            if (data.has("data")) data = data.getJSONArray("data").getJSONObject(0);

            String description = data.getString("description").equals("") ? devi.getTranslation(command.getLanguage(), 212) : data.getString("description");
            String words[] = description.split("[ \n]");

            String fixedDescription = Arrays.stream(words).limit(20).collect(Collectors.joining(" "));
            if (words.length > 20) fixedDescription += " ...";

            builder.addField(data.getString("display_name"), fixedDescription, false);
        }

        sender.reply(builder.build());
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 200;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("liststreamer", "listtwitch");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.TWITCH;
    }
}
