package me.purox.devi.commands.guild;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.client.result.DeleteResult;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bson.Document;
import org.json.JSONObject;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RemoveStreamCommandExecutor implements CommandExecutor {

    private Devi devi;
    public RemoveStreamCommandExecutor(Devi devi) {
        this.devi = devi;
    }


    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 1) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "removestream <stream>`"));
            return;
        }

        String baseUrl = "https://api.twitch.tv/helix/users";
        String search = args[0];

        try {
            Request.JSONResponse response = new RequestBuilder(devi.getOkHttpClient()).setURL(baseUrl + "?login=" + search)
                    .addHeader("Client-ID", devi.getSettings().getTwitchClientID())
                    .addHeader("Authorization", "Bearer " + devi.getSettings().getTwitchSecret())
                    .setRequestType(Request.RequestType.GET).build()
                    .asJSONSync();
            JSONObject body = response.getBody();

            if (response.getStatus() == 429) {
                sender.reply(devi.getTranslation(command.getLanguage(), 210));
                return;
            }

            if (body.getJSONArray("data").length() == 0) {
                sender.reply(devi.getTranslation(command.getLanguage(), 203, "`" + search + "`"));
                return;
            }

            JSONObject user = body.getJSONArray("data").getJSONObject(0);
            String id = user.getString("id");
            AtomicBoolean doesStreamerExist = new AtomicBoolean(false);

            command.getDeviGuild().getStreams().forEach(document -> {
                if (document.getString("stream").equals(id)) {
                    doesStreamerExist.set(true);
                }
            });

            if (!doesStreamerExist.get()) {
                sender.reply(devi.getTranslation(command.getLanguage(), 214, user.getString("display_name")));
                return;
            }

            Document document = command.getDeviGuild().getStreams().stream().filter(doc -> doc.getString("stream").equals(id)).findAny().orElse(null);

            if (document == null) {
                sender.reply(devi.getTranslation(command.getLanguage(), 202, "<https://www.devibot.net/support>"));
                return;
            }

            if (devi.getDatabaseManager().removeFromDatabase("streams", document.getString("_id")).wasAcknowledged()) {
                //not in the streamer map for some reason?!
                if (!devi.getStreams().containsKey(id)) {
                    devi.changeTwitchSubscriptionStatus(Collections.singleton(id), false);
                }
                //this was the only guild that got notifications for that streamer
                else if (devi.getStreams().get(id).size() == 1){
                    devi.getStreams().remove(id);
                    devi.changeTwitchSubscriptionStatus(Collections.singleton(id), false);
                }
                //other guilds still get notifications for that streamer
                else {
                    devi.getStreams().get(id).remove(command.getDeviGuild().getId());
                }

                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(new Color(100, 65, 164));
                builder.setAuthor("Twitch Stream", null, "https://www.twitch.tv/p/assets/uploads/glitch_474x356.png");
                builder.setThumbnail(user.getString("profile_image_url"));
                builder.appendDescription(devi.getTranslation(command.getLanguage(), 215, user.getString("display_name")) + " ");

                command.getDeviGuild().getStreams().remove(document);
                sender.reply(builder.build());
                devi.getRedisSender().hset("streams#1", id, user.toString());
            } else {
                sender.reply(devi.getTranslation(command.getLanguage(), 202, "<https://www.devibot.net/support>"));
            }
        } catch (IllegalArgumentException e) {
            sender.reply(devi.getTranslation(command.getLanguage(), 203, "`" + search + "`"));
        }
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 201;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("rmstream", "removetwitch", "rmtwitch");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
