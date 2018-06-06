package me.purox.devi.commands.guild;

import com.mongodb.client.result.UpdateResult;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddStreamCommandExecutor implements CommandExecutor {

    private Devi devi;
    public AddStreamCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        if (args.length < 1) {
            sender.reply(devi.getTranslation(command.getLanguage(), 12, "`" + command.getPrefix() + "addstream <stream>`"));
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

            if (doesStreamerExist.get()) {
                sender.reply(devi.getTranslation(command.getLanguage(), 204, user.getString("display_name")));
                return;
            }

            Document document = new Document();
            document.put("stream", id);
            document.put("guild", command.getDeviGuild().getId());

            UpdateResult updateResult = devi.getDatabaseManager().saveToDatabase("streams", document);
            if (updateResult.wasAcknowledged()) {
                document.put("_id", updateResult.getUpsertedId().asString().getValue());
                if (devi.getStreams().containsKey(id)) {
                    devi.getStreams().get(id).add(command.getDeviGuild().getId());
                } else {
                    devi.getStreams().put(id, Collections.singletonList(command.getDeviGuild().getId()));
                    devi.changeTwitchSubscriptionStatus(Collections.singleton(id), true);
                }
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(new Color(100, 65, 164));
                builder.setAuthor("Twitch Stream", null, "https://www.twitch.tv/p/assets/uploads/glitch_474x356.png");
                builder.setThumbnail(user.getString("profile_image_url"));
                builder.appendDescription(devi.getTranslation(command.getLanguage(), 205, user.getString("display_name")) + " ");

                TextChannel textChannel = DiscordUtils.getTextChannel(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.TWITCH_CHANNEL), command.getEvent().getGuild());
                if (textChannel == null)
                    builder.appendDescription(devi.getTranslation(command.getLanguage(), 206, "`" + command.getPrefix() + "settings twitch_channel <channel>`"));
                else
                    builder.appendDescription(devi.getTranslation(command.getLanguage(), 207, textChannel.getAsMention()));

                command.getDeviGuild().getStreams().add(document);
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
        return 199;
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("addtwitch", "addstreamer");
    }

    @Override
    public Permission getPermission() {
        return Permission.MANAGE_SERVER;
    }
}
