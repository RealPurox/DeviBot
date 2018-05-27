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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FortniteCommandExecutor implements CommandExecutor {

    private Devi devi;

    private HashMap<String, String> platforms = new HashMap<>();

    public FortniteCommandExecutor(Devi devi) {
        this.devi = devi;

        this.platforms.put("pc", "pc");
        this.platforms.put("computer", "pc");
        this.platforms.put("personal_computer", "pc");
        this.platforms.put("personalcomputer", "pc");

        this.platforms.put("xb1", "xbl");
        this.platforms.put("xbox", "xbl");
        this.platforms.put("box", "xbl");
        this.platforms.put("xbox1", "xbl");
        this.platforms.put("xboxone", "xbl");
        this.platforms.put("xbox_one", "xbl");

        this.platforms.put("psn", "psn");
        this.platforms.put("station", "psn");
        this.platforms.put("play_station", "psn");
        this.platforms.put("ps4", "psn");
        this.platforms.put("playstation", "psn");
        this.platforms.put("pspro", "psn");
        this.platforms.put("playstationpro", "psn");
        this.platforms.put("play_station_pro", "psn");
        this.platforms.put("playstation_pro", "psn");
        this.platforms.put("play_stationpro", "psn");
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 2) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "fortnite <platform> <epic-name>`"));
            return;
        }

        if (!platforms.containsKey(args[0])) {
            sender.reply(devi.getTranslation(command.getLanguage(), 246, "`" + args[0] + "`"));
            return;
        }

        String search = Arrays.stream(args).skip(1).collect(Collectors.joining("%20"));
        String game = platforms.get(args[0]);

        String URL = "https://api.fortnitetracker.com/v1/profile/" + game + "/" + search;

        HashMap<String, String> headers = new HashMap<>();
        headers.put("TRN-Api-Key", devi.getSettings().getFortniteApiKey());

        Unirest.get(URL).headers(headers).asJsonAsync(new Callback<JsonNode>() {
            @Override
            public void completed(HttpResponse<JsonNode> response) {
                JSONObject data = response.getBody().getObject();
                System.out.println(data);

                if (data.has("error") && data.getString("error").equals("Player Not Found")) {
                    sender.reply(devi.getTranslation(command.getLanguage(), 219, "`" + search + "`"));
                    return;
                }

                JSONObject solo = data.getJSONObject("stats").getJSONObject("p2");
                JSONObject duo = data.getJSONObject("stats").getJSONObject("p10");
                JSONObject squad = data.getJSONObject("stats").getJSONObject("p9");

                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(new java.awt.Color(100, 65, 164));
                builder.setAuthor(devi.getTranslation(command.getLanguage(), 237, data.getString("epicUserHandle")),
                        null, "https://images.gutefrage.net/media/fragen/bilder/welche-dpi-fuer-fortnite/0_big.jpg");

                String soloBuilder = "**" + devi.getTranslation(command.getLanguage(), 238) + "**: " + solo.getJSONObject("top1").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 239) + "**: " + solo.getJSONObject("winRatio").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 240) + "**: " + solo.getJSONObject("matches").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 241) + "**: " + solo.getJSONObject("kills").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 242) + "**: " + solo.getJSONObject("kd").getString("value") + "\n";
                builder.addField(devi.getTranslation(command.getLanguage(), 243), soloBuilder, true);

                String duoBuilder = "**" + devi.getTranslation(command.getLanguage(), 238) + "**: " + duo.getJSONObject("top1").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 239) + "**: " + duo.getJSONObject("winRatio").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 240) + "**: " + duo.getJSONObject("matches").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 241) + "**: " + duo.getJSONObject("kills").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 242) + "**: " + duo.getJSONObject("kd").getString("value") + "\n";
                builder.addField(devi.getTranslation(command.getLanguage(), 244), duoBuilder, true);

                String squadBuilder = "**" + devi.getTranslation(command.getLanguage(), 238) + "**: " + squad.getJSONObject("top1").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 239) + "**: " + squad.getJSONObject("winRatio").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 240) + "**: " + squad.getJSONObject("matches").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 241) + "**: " + squad.getJSONObject("kills").getString("value") + "\n" +
                        "**" + devi.getTranslation(command.getLanguage(), 242) + "**: " + squad.getJSONObject("kd").getString("value") + "\n";
                builder.addField(devi.getTranslation(command.getLanguage(), 245), squadBuilder, true);

                sender.reply(builder.build());
            }

            @Override
            public void failed(UnirestException e) {
                e.printStackTrace();
            }

            @Override
            public void cancelled() {
                System.out.println("CANCELLED");
            }
        });
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 229;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("fortnitestats", "ftn");
    }

    @Override
    public Permission getPermission() {
        return null;
    }
}
