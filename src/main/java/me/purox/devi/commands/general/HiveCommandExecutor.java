package me.purox.devi.commands.general;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.utils.TimeUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.json.JSONObject;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class HiveCommandExecutor implements CommandExecutor {

    private Devi devi;
    public HiveCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length == 0) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "hive <player>`"));
            return;
        }

        String search = Arrays.stream(args).collect(Collectors.joining(" "));
        try {
            String URL = "http://api.hivemc.com/v1/player/" + search;
            HttpResponse<String> response = Unirest.get(URL).asString();

            if (response.getStatus() == 404) {
                sender.reply(devi.getTranslation(command.getLanguage(), 219, "`" + search + "`"));
                return;
            }

            JSONObject player = new JSONObject(response.getBody());
            JSONObject status = player.getJSONObject("status");

            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor(devi.getTranslation(command.getLanguage(), 227, player.getString("username")), null, "https://i.imgur.com/VQJW6XG.png");
            builder.setColor(Color.YELLOW);
            builder.setThumbnail("https://cravatar.eu/helmavatar/" + search + "/64.png");
            builder.addField("Rank", player.getJSONObject("modernRank").getString("human"), true);
            builder.addField("Tokens", player.getInt("tokens") + "", true);
            builder.addField("Credits", player.getInt("credits") + "", true);
            builder.addField("Crates", player.isNull("crates") ? "0" : player.getInt("crates") + "", true);
            builder.addField("Medals", player.getInt("medals") + "", true);
            builder.addField("Achievements", player.getJSONObject("achievements").keySet().size() + "", true);
            builder.addField("Status", status.getString("description") + " " + status.getString("game"), true);

            sender.reply(builder.build());
        } catch (UnirestException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 217));
        } catch (IllegalArgumentException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 219, "`" + search + "`"));
        }
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 226;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("hivestats", "hievplayer");
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
