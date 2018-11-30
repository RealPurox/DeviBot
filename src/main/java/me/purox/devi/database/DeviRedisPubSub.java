package me.purox.devi.database;

import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.entities.Language;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.JedisPubSub;

import java.awt.*;

public class DeviRedisPubSub extends JedisPubSub {

    private Devi devi;

    DeviRedisPubSub(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onMessage(String channel, String message) {
        devi.getLogger().log("Received a message from the website: " + message);
        //<editor-fold desc="devi_twitch_event channel">
        if (channel.equals("devi_twitch_event")) {
            JSONObject object = new JSONArray(message).getJSONObject(0);

            if (devi.getStreams().containsKey(object.getString("user_id"))) {
                for (String guildID : devi.getStreams().get(object.getString("user_id"))) {
                    if (devi.getShardManager() == null) return;

                    DeviGuild deviGuild = devi.getDeviGuild(guildID);

                    Guild guild = null;
                    for (JDA jda : devi.getShardManager().getShards()) {
                        for (Guild g : jda.getGuilds()) {
                            if (g.getId().equals(guildID)) {
                                guild = g;
                                break;
                            }
                        }
                    }
                    if (guild == null) return;

                    TextChannel textChannel = DiscordUtils.getTextChannel(deviGuild.getSettings().getStringValue(GuildSettings.Settings.TWITCH_CHANNEL), guild);
                    if (textChannel == null) return;

                    Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

                    JSONObject user = new RequestBuilder(devi.getOkHttpClient()).setURL("https://api.twitch.tv/helix/users?id=" + object.getString("user_id"))
                            .addHeader("Client-ID", devi.getSettings().getTwitchClientID())
                            .addHeader("Authorization", "Bearer " + devi.getSettings().getTwitchSecret())
                            .setRequestType(Request.RequestType.GET).build().asJSONSync().getBody();

                    JSONObject game = new RequestBuilder(devi.getOkHttpClient()).setURL("https://api.twitch.tv/helix/games?id=" + object.getString("game_id"))
                            .addHeader("Client-ID", devi.getSettings().getTwitchClientID())
                            .addHeader("Authorization", "Bearer " + devi.getSettings().getTwitchSecret())
                            .setRequestType(Request.RequestType.GET).build().asJSONSync().getBody();

                    if (user.getJSONArray("data").length() == 0) return;
                    JSONObject userData = user.getJSONArray("data").getJSONObject(0);
                    String url = "https://www.twitch.tv/" + userData.getString("login");

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(new Color(100, 65, 164));
                    builder.setAuthor(userData.getString("display_name"), url, "https://www.twitch.tv/p/assets/uploads/glitch_474x356.png");
                    builder.setImage(object.getString("thumbnail_url").replace("{width}", "480").replace("{height}", "270"));
                    builder.setDescription(object.getString("title"));
                    builder.addField(devi.getTranslation(language, 208), String.valueOf(object.getInt("viewer_count")), true);

                    if (game.getJSONArray("data").length() != 0)
                        builder.addField(devi.getTranslation(language, 209), game.getJSONArray("data").getJSONObject(0).getString("name"), true);

                    builder.setThumbnail(userData.getString("profile_image_url"));

                    devi.getLogger().log("Sending stream announcement for twitch streamer " +
                            userData.getString("display_name") + " (" + object.getString("user_id") + ") to guild " + guild.getName() + " (" + guild.getId() + " )");
                    MessageUtils.sendMessageAsync(textChannel, new MessageBuilder()
                            .setContent(devi.getTranslation(language, 211, userData.getString("display_name"), url))
                            .setEmbed(builder.build()).build());
                    devi.getRedisManager().getSender().hset("streams#1", object.getString("user_id"), userData.toString());
                }
            }
        }
        //</editor-fold>
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        devi.getLogger().log("Subscribed to redis channel " + channel);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        devi.getLogger().log("Unsubscribe from redis channel " + channel);
    }

}
