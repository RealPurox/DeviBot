package me.purox.devi.commands.general;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
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
import java.util.List;
import java.util.stream.Collectors;

public class HypixelCommandExecutor implements CommandExecutor {

    private Devi devi;
    public HypixelCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length == 0) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "hypixel <player>`"));
            return;
        }

        String search = Arrays.stream(args).collect(Collectors.joining(" "));
        String URL = "https://api.hypixel.net/player?key=" + devi.getSettings().getHypixelAPIKey() + "&name=" + search;

        try {
            Unirest.get(URL).asJsonAsync(new Callback<JsonNode>() {
                @Override
                public void completed(HttpResponse<JsonNode> response) {
                    JSONObject data = response.getBody().getObject();

                    if (data.isNull("player")) {
                        sender.reply(devi.getTranslation(command.getLanguage(), 219, "`" + search + "`"));
                        return;
                    }

                    JSONObject player = data.getJSONObject("player");

                    int level = getLevel(player.getInt("networkExp"));
                    String rank = getRank(player);
                    String firstLogin = TimeUtils.toRelative(System.currentTimeMillis() - player.getLong("firstLogin"), 1);
                    String lastLogin = TimeUtils.toRelative(System.currentTimeMillis() - player.getLong("lastLogin"), 1);

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setAuthor(devi.getTranslation(command.getLanguage(), 220, player.getString("displayname")), null, "https://vignette.wikia.nocookie.net/youtube/images/b/bb/Hypixel.jpeg/revision/latest?cb=20151112183800");
                    builder.setColor(Color.YELLOW);
                    builder.setThumbnail("https://cravatar.eu/helmavatar/" + search + "/64.png");

                    builder.addField(devi.getTranslation(command.getLanguage(), 221), String.valueOf(level), true);
                    builder.addField(devi.getTranslation(command.getLanguage(), 230), String.valueOf(player.getInt("karma")), true);
                    builder.addField(devi.getTranslation(command.getLanguage(), 222), rank, true);
                    builder.addField(devi.getTranslation(command.getLanguage(), 224), firstLogin, true);
                    builder.addField(devi.getTranslation(command.getLanguage(), 223), lastLogin, true);

                    sender.reply(builder.build());
                }

                @Override
                public void failed(UnirestException e) {
                    sender.reply(devi.getTranslation(command.getLanguage(), 217));
                }

                @Override
                public void cancelled() {
                    sender.reply(devi.getTranslation(command.getLanguage(), 217));
                }
            });
        } catch (IllegalArgumentException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 219, "`" + search + "`"));
        }
    }

    private int getLevel(int xp) {
        double reservePqPrefix = -3.5;
        double reserveConst = 12.25;
        double growthDivides2 = 0.0008;
        return xp < 0 ? 1 : (int) Math.floor(1 + reservePqPrefix + Math.sqrt(reserveConst + growthDivides2 * xp));
    }

    private String getRank(JSONObject player) {
        String rank = "MEMBER";
        if (player.has("newPackageRank"))
            rank = player.getString("newPackageRank").replace("_PLUS", "+");
        if (player.has("monthlyPackageRank"))
            rank = "MVP++";
        if (player.has("rank"))
            rank = player.getString("rank");
        return rank;
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 225;
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
