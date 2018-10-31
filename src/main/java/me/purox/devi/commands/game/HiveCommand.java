package me.purox.devi.commands.game;

import me.purox.devi.commands.ICommand;
import me.purox.devi.commands.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import org.json.JSONObject;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class HiveCommand extends ICommand {

    private Devi devi;

    public HiveCommand(Devi devi) {
        super("hive", "hivestats", "hiveplayer");
        this.devi = devi;
    }

    @Override
    public void execute(CommandSender sender, Command command) {
        if (command.getArgs().length == 0) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "hive <player>`"));
            return;
        }

        String search = Arrays.stream(command.getArgs()).collect(Collectors.joining(" "));
        String URL = "http://api.hivemc.com/v1/player/" + search;

        new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET).setURL(URL).build()
                .asString(response -> {
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
                    builder.addField(devi.getTranslation(command.getLanguage(), 222), player.getJSONObject("modernRank").getString("human"), true);
                    builder.addField(devi.getTranslation(command.getLanguage(), 231), player.getInt("tokens") + "", true);
                    builder.addField(devi.getTranslation(command.getLanguage(), 232), player.getInt("credits") + "", true);
                    builder.addField(devi.getTranslation(command.getLanguage(), 233), player.isNull("crates") ? "0" : player.getInt("crates") + "", true);
                    builder.addField(devi.getTranslation(command.getLanguage(), 234), player.getInt("medals") + "", true);
                    builder.addField(devi.getTranslation(command.getLanguage(), 235), player.isNull("achievements") ? "0" : player.getJSONObject("achievements").keySet().size() + "", true);
                    builder.addField(devi.getTranslation(command.getLanguage(), 236), status.getString("description") + " " + status.getString("game"), true);

                    sender.reply(builder.build());
                }, error -> sender.reply(devi.getTranslation(command.getLanguage(), 217)));
    }
}
