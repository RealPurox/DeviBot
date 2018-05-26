package me.purox.devi.commands.info;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

        String search = args[0];
        try {
            String URL = "http://api.hivemc.com/v1/player/" + search;
            HttpResponse<String> response = Unirest.get(URL).asString();

            if (response.getStatus() == 404) {
                sender.reply(devi.getTranslation(command.getLanguage(), 219, "`" + search + "`"));
                return;
            }

            JSONObject player = new JSONObject(response.getBody());
            System.out.println(player);

            String firstLogin = TimeUtils.toRelative(new Date(player.getLong("firstLogin")), new Date(), 1);
            String lastLogin = TimeUtils.toRelative(new Date(player.getLong("lastLogin")), new Date(), 1);

            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor(devi.getTranslation(command.getLanguage(), 227, player.getString("username")), null, "https://forum.hivemc.com/styles/hive-mc/style/logo.png");
            builder.setColor(Color.YELLOW);
            builder.setThumbnail("https://cravatar.eu/helmavatar/" + search + "/64.png");
            builder.addField(devi.getTranslation(command.getLanguage(), 224), firstLogin, true);
            builder.addField(devi.getTranslation(command.getLanguage(), 223), lastLogin, true);

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
