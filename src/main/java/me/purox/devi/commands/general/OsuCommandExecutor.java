package me.purox.devi.commands.general;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.waiter.WaitingResponse;
import me.purox.devi.core.waiter.WaitingResponseBuilder;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class OsuCommandExecutor implements CommandExecutor {

    private Devi devi;

    public OsuCommandExecutor(Devi devi) {
        this.devi = devi;
    }
    @SuppressWarnings("Duplicates")
    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length == 0) {
            sender.reply(DeviEmote.ERROR + devi.getTranslation(command.getLanguage(), 513, command.getPrefix() + "osu <user> [standard/taiko/ctb/mania]"));
            return;
        }

        String baseUrl = "https://osu.ppy.sh/api/get_user?k=" + devi.getSettings().getOsuApiKey() + "&u=";
        String modeStandrad = "&m=0";
        String modeTaiko = "&m=1";
        String modeCtb = "&m=2";
        String modeMania = "&m=3";

        if (args.length == 1) {
            String inputUser = args[0];

            //I am deeply and truly sorry for coding it like this Daniel. I know you will hate me. Sorry. :heart:
            //fix it if you want bitch

            try {
                Request.StringResponse res = new RequestBuilder(devi.getOkHttpClient()).setURL(baseUrl + inputUser + modeStandrad).setRequestType(Request.RequestType.GET).build().asStringSync();

                JSONArray body = new JSONArray(res.getBody());

                if (res.getStatus() == 429 || body.length() <= 0) {
                    sender.reply(DeviEmote.ERROR + devi.getTranslation(command.getLanguage(), 514, inputUser));
                    return;
                }
                JSONObject user = new JSONArray(res.getBody()).getJSONObject(0);

                DecimalFormat df = new DecimalFormat("##.##");

                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.decode("#dc98a4"));
                embed.setAuthor(user.getString("username") + " - Standard", null, "https://i.imgur.com/S487amt.png");
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 515) + "**", user.isNull("user_id")? "0" : String.valueOf(user.getLong("user_id")), true);
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 516) + "**", user.isNull("level")? "0" : String.valueOf(user.getDouble("level")), true);
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 517) + "**", user.getString("country").equals("") ? "None" : ":flag_" + user.getString("country").toLowerCase() + ": " + user.getString("country"), true);
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 518) + "**", user.isNull("pp_raw")? "0" : String.valueOf(user.getDouble("pp_raw")), true);
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 519) + "**", user.isNull("pp_rank")? "0" : String.valueOf(user.getLong("pp_rank")), true);
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 520) + "**", user.isNull("accuracy")? "0" : df.format(user.getDouble("accuracy")) + "%", true);
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 521) + "**", user.isNull("playcount")? "0 times" : String.valueOf(user.getLong("playcount") + " times"), true);
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 522) + "**", user.isNull("pp_country_rank")? "0" : String.valueOf(user.getLong("pp_country_rank")), true);
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 523) + "**", user.isNull("total_score")? "0" : String.valueOf(user.getLong("total_score") + " \n" + "_" + String.valueOf(user.getLong("ranked_score") + " " + devi.getTranslation(command.getLanguage(), 524) + "._")), true);
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 525) + "**", user.isNull("count_rank_ss")? "0" : String.valueOf(user.getLong("count_rank_ss")), true);
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 526) + "**", user.isNull("count_rank_s")? "0" : String.valueOf(user.getLong("count_rank_s")), true);
                embed.addField("**" + devi.getTranslation(command.getLanguage(), 527) + "**", user.isNull("count_rank_a")? "0" : String.valueOf(user.getLong("count_rank_a")), true);
                sender.reply(embed.build());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        if (args.length >= 2) {
            AtomicReference<String> inputUser = new AtomicReference<>(null);

            inputUser.set(args[0]);

            switch (args[1].toLowerCase()) {

                case "taiko":
                case "1":
                    try {
                        Request.StringResponse res = new RequestBuilder(devi.getOkHttpClient()).setURL(baseUrl + inputUser + modeTaiko).setRequestType(Request.RequestType.GET).build().asStringSync();
                        JSONArray body = new JSONArray(res.getBody());
                        if (res.getStatus() == 429 || body.length() <= 0) {
                            sender.reply(DeviEmote.ERROR + devi.getTranslation(command.getLanguage(), 514, inputUser));
                            return;
                        }
                        JSONObject user = new JSONArray(res.getBody()).getJSONObject(0);

                        DecimalFormat df = new DecimalFormat("##.##");

                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(Color.decode("#dc98a4"));
                        embed.setAuthor(user.getString("username") + "- Taiko", null, "https://i.imgur.com/S487amt.png");
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 515) + "**", user.isNull("user_id")? "0" : String.valueOf(user.getLong("user_id")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 516) + "**", user.isNull("level")? "0" : String.valueOf(user.getDouble("level")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 517) + "**", user.getString("country").equals("") ? "None" : ":flag_" + user.getString("country").toLowerCase() + ": " + user.getString("country"), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 518) + "**", user.isNull("pp_raw")? "0" : String.valueOf(user.getDouble("pp_raw")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 519) + "**", user.isNull("pp_rank")? "0" : String.valueOf(user.getLong("pp_rank")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 520) + "**", user.isNull("accuracy")? "0" : df.format(user.getDouble("accuracy")) + "%", true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 521) + "**", user.isNull("playcount")? "0 times" : String.valueOf(user.getLong("playcount") + " times"), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 522) + "**", user.isNull("pp_country_rank")? "0" : String.valueOf(user.getLong("pp_country_rank")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 523) + "**", user.isNull("total_score")? "0" : String.valueOf(user.getLong("total_score") + " \n" + "_" + String.valueOf(user.getLong("ranked_score") + " " + devi.getTranslation(command.getLanguage(), 524) + "._")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 525) + "**", user.isNull("count_rank_ss")? "0" : String.valueOf(user.getLong("count_rank_ss")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 526) + "**", user.isNull("count_rank_s")? "0" : String.valueOf(user.getLong("count_rank_s")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 527) + "**", user.isNull("count_rank_a")? "0" : String.valueOf(user.getLong("count_rank_a")), true);
                        sender.reply(embed.build());
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }
                    break;
                case "ctb": case "2":
                    try {
                        Request.StringResponse res = new RequestBuilder(devi.getOkHttpClient()).setURL(baseUrl + inputUser + modeCtb).setRequestType(Request.RequestType.GET).build().asStringSync();
                        JSONArray body = new JSONArray(res.getBody());
                        if (res.getStatus() == 429 || body.length() <= 0) {
                            sender.reply(DeviEmote.ERROR + devi.getTranslation(command.getLanguage(), 514, inputUser));
                            return;
                        }
                        JSONObject user = new JSONArray(res.getBody()).getJSONObject(0);

                        DecimalFormat df = new DecimalFormat("##.##");

                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(Color.decode("#dc98a4"));
                        embed.setAuthor(user.getString("username") + " - Catch The Beat", null, "https://i.imgur.com/S487amt.png");
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 515) + "**", user.isNull("user_id")? "0" : String.valueOf(user.getLong("user_id")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 516) + "**", user.isNull("level")? "0" : String.valueOf(user.getDouble("level")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 517) + "**", user.getString("country").equals("") ? "None" : ":flag_" + user.getString("country").toLowerCase() + ": " + user.getString("country"), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 518) + "**", user.isNull("pp_raw")? "0" : String.valueOf(user.getDouble("pp_raw")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 519) + "**", user.isNull("pp_rank")? "0" : String.valueOf(user.getLong("pp_rank")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 520) + "**", user.isNull("accuracy")? "0" : df.format(user.getDouble("accuracy")) + "%", true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 521) + "**", user.isNull("playcount")? "0 times" : String.valueOf(user.getLong("playcount") + " times"), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 522) + "**", user.isNull("pp_country_rank")? "0" : String.valueOf(user.getLong("pp_country_rank")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 523) + "**", user.isNull("total_score")? "0" : String.valueOf(user.getLong("total_score") + " \n" + "_" + String.valueOf(user.getLong("ranked_score") + " " + devi.getTranslation(command.getLanguage(), 524) + "._")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 525) + "**", user.isNull("count_rank_ss")? "0" : String.valueOf(user.getLong("count_rank_ss")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 526) + "**", user.isNull("count_rank_s")? "0" : String.valueOf(user.getLong("count_rank_s")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 527) + "**", user.isNull("count_rank_a")? "0" : String.valueOf(user.getLong("count_rank_a")), true);
                        sender.reply(embed.build());
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }                    break;
                case "mania": case "3":
                    try {
                        Request.StringResponse res = new RequestBuilder(devi.getOkHttpClient()).setURL(baseUrl + inputUser + modeMania).setRequestType(Request.RequestType.GET).build().asStringSync();
                        JSONArray body = new JSONArray(res.getBody());
                        if (res.getStatus() == 429 || body.length() <= 0) {
                            sender.reply(DeviEmote.ERROR + devi.getTranslation(command.getLanguage(), 514, inputUser));
                            return;
                        }
                        JSONObject user = new JSONArray(res.getBody()).getJSONObject(0);

                        DecimalFormat df = new DecimalFormat("##.##");

                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(Color.decode("#dc98a4"));
                        embed.setAuthor(user.getString("username") + " - osu! Mania", null, "https://i.imgur.com/S487amt.png");
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 515) + "**", user.isNull("user_id")? "0" : String.valueOf(user.getLong("user_id")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 516) + "**", user.isNull("level")? "0" : String.valueOf(user.getDouble("level")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 517) + "**", user.getString("country").equals("") ? "None" : ":flag_" + user.getString("country").toLowerCase() + ": " + user.getString("country"), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 518) + "**", user.isNull("pp_raw")? "0" : String.valueOf(user.getDouble("pp_raw")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 519) + "**", user.isNull("pp_rank")? "0" : String.valueOf(user.getLong("pp_rank")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 520) + "**", user.isNull("accuracy")? "0" : df.format(user.getDouble("accuracy")) + "%", true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 521) + "**", user.isNull("playcount")? "0 times" : String.valueOf(user.getLong("playcount") + " times"), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 522) + "**", user.isNull("pp_country_rank")? "0" : String.valueOf(user.getLong("pp_country_rank")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 523) + "**", user.isNull("total_score")? "0" : String.valueOf(user.getLong("total_score") + " \n" + "_" + String.valueOf(user.getLong("ranked_score") + " " + devi.getTranslation(command.getLanguage(), 524) + "._")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 525) + "**", user.isNull("count_rank_ss")? "0" : String.valueOf(user.getLong("count_rank_ss")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 526) + "**", user.isNull("count_rank_s")? "0" : String.valueOf(user.getLong("count_rank_s")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 527) + "**", user.isNull("count_rank_a")? "0" : String.valueOf(user.getLong("count_rank_a")), true);
                        sender.reply(embed.build());
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }
                    break;
                default:
                    try {
                        Request.StringResponse res = new RequestBuilder(devi.getOkHttpClient()).setURL(baseUrl + inputUser + modeStandrad).setRequestType(Request.RequestType.GET).build().asStringSync();
                        JSONArray body = new JSONArray(res.getBody());
                        if (res.getStatus() == 429 || body.length() <= 0) {
                            sender.reply(DeviEmote.ERROR + devi.getTranslation(command.getLanguage(), 514, inputUser));
                            return;
                        }
                        JSONObject user = new JSONArray(res.getBody()).getJSONObject(0);

                        DecimalFormat df = new DecimalFormat("##.##");

                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setColor(Color.decode("#dc98a4"));
                        embed.setAuthor(user.getString("username") + " - Standard", null, "https://i.imgur.com/S487amt.png");
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 515) + "**", user.isNull("user_id")? "0" : String.valueOf(user.getLong("user_id")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 516) + "**", user.isNull("level")? "0" : String.valueOf(user.getDouble("level")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 517) + "**", user.getString("country").equals("") ? "None" : ":flag_" + user.getString("country").toLowerCase() + ": " + user.getString("country"), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 518) + "**", user.isNull("pp_raw")? "0" : String.valueOf(user.getDouble("pp_raw")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 519) + "**", user.isNull("pp_rank")? "0" : String.valueOf(user.getLong("pp_rank")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 520) + "**", user.isNull("accuracy")? "0" : df.format(user.getDouble("accuracy")) + "%", true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 521) + "**", user.isNull("playcount")? "0 times" : String.valueOf(user.getLong("playcount") + " times"), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 522) + "**", user.isNull("pp_country_rank")? "0" : String.valueOf(user.getLong("pp_country_rank")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 523) + "**", user.isNull("total_score")? "0" : String.valueOf(user.getLong("total_score") + " \n" + "_" + String.valueOf(user.getLong("ranked_score") + " " + devi.getTranslation(command.getLanguage(), 524) + "._")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 525) + "**", user.isNull("count_rank_ss")? "0" : String.valueOf(user.getLong("count_rank_ss")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 526) + "**", user.isNull("count_rank_s")? "0" : String.valueOf(user.getLong("count_rank_s")), true);
                        embed.addField("**" + devi.getTranslation(command.getLanguage(), 527) + "**", user.isNull("count_rank_a")? "0" : String.valueOf(user.getLong("count_rank_a")), true);
                        sender.reply(embed.build());
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public boolean guildOnly() {
        return false;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 512;
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.GAME_COMMANDS;
    }
}
