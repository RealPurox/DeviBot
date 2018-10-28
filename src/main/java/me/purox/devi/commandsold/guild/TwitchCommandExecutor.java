package me.purox.devi.commandsold.guild;

import com.mongodb.client.result.UpdateResult;
import me.purox.devi.commandsold.handler.ICommand;
import me.purox.devi.commandsold.handler.CommandExecutor;
import me.purox.devi.commandsold.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.ModuleType;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.core.guild.entities.Stream;
import me.purox.devi.core.waiter.WaitingResponse;
import me.purox.devi.core.waiter.WaitingResponseBuilder;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bson.Document;
import org.json.JSONObject;

import java.awt.*;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class TwitchCommandExecutor implements CommandExecutor {

    private Devi devi;

    public TwitchCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, ICommand command, CommandSender sender) {
        TextChannel textChannel = DiscordUtils.getTextChannel(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.TWITCH_CHANNEL), command.getEvent().getGuild());

        WaitingResponse addStreamer = new WaitingResponseBuilder(devi, command)
                .setReplyText("")
                .setWaiterType(WaitingResponseBuilder.WaiterType.CUSTOM)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 363))
                .setTryAgainAfterCustomCheckFail(true)
                .withCustomCheck((response) -> {
                    String baseUrl = "https://api.twitch.tv/helix/users";
                    String search = response.getMessage().getContentRaw().split(" ")[0];
                    try {
                        Request.JSONResponse res = new RequestBuilder(devi.getOkHttpClient()).setURL(baseUrl + "?login=" + search)
                                .addHeader("Client-ID", devi.getSettings().getTwitchClientID())
                                .addHeader("Authorization", "Bearer " + devi.getSettings().getTwitchSecret())
                                .setRequestType(Request.RequestType.GET).build()
                                .asJSONSync();
                        JSONObject body = res.getBody();

                        if (res.getStatus() == 429 || body.isNull("data") || body.getJSONArray("data").length() == 0) {
                            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 366, search));
                            return null;
                        }

                        JSONObject user = body.getJSONArray("data").getJSONObject(0);
                        String id = user.getString("id");
                        AtomicBoolean doesStreamerExist = new AtomicBoolean(false);

                        command.getDeviGuild().getStreams().forEach(stream -> {
                            if (stream.getStream().equals(id)) {
                                doesStreamerExist.set(true);
                            }
                        });

                        if (doesStreamerExist.get()) {
                            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 204, user.getString("display_name")));
                            return null;
                        }
                        return new AbstractMap.SimpleEntry<>(id, user);
                    } catch (IllegalArgumentException e) {
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 203, "`" + search + "`"));
                        return null;
                    }
                })
                .withCustomVoid(object -> {
                    if (object == null) return;
                    String id = ((Map.Entry<String, JSONObject>)object).getKey();
                    JSONObject user = ((Map.Entry<String, JSONObject>)object).getValue();

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

                        TextChannel channel = DiscordUtils.getTextChannel(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.TWITCH_CHANNEL), command.getEvent().getGuild());
                        if (channel == null)
                            builder.appendDescription(devi.getTranslation(command.getLanguage(), 206, "`" + command.getPrefix() + "twitch`"));
                        else
                            builder.appendDescription(devi.getTranslation(command.getLanguage(), 207, channel.getAsMention()));

                        command.getDeviGuild().getStreams().add(Devi.GSON.fromJson(document.toJson(), Stream.class));
                        sender.reply(builder.build());
                        devi.getRedisSender().hset("streams#1", id, user.toString());
                        devi.getLogger().log("Added twitch streamer " + user.getString("display_name") + " (" + id + ") to guild " + command.getEvent().getGuild().getName() + " (" + command.getEvent().getGuild().getId() + ")");
                    } else {
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 202, "<https://www.devibot.net/support>"));
                    }
                })
                .build();

        WaitingResponse removeStreamer = new WaitingResponseBuilder(devi, command)
                .setReplyText("")
                .setWaiterType(WaitingResponseBuilder.WaiterType.CUSTOM)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 364))
                .setTryAgainAfterCustomCheckFail(true)
                .withCustomCheck(response -> {
                    String baseUrl = "https://api.twitch.tv/helix/users";
                    String search = response.getMessage().getContentRaw().split(" ")[0];

                    try {
                        Request.JSONResponse res = new RequestBuilder(devi.getOkHttpClient()).setURL(baseUrl + "?login=" + search)
                                .addHeader("Client-ID", devi.getSettings().getTwitchClientID())
                                .addHeader("Authorization", "Bearer " + devi.getSettings().getTwitchSecret())
                                .setRequestType(Request.RequestType.GET).build()
                                .asJSONSync();
                        JSONObject body = res.getBody();

                        if (res.getStatus() == 429 || body.isNull("data") || body.getJSONArray("data").length() == 0) {
                            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 366, search));
                            return null;
                        }

                        JSONObject user = body.getJSONArray("data").getJSONObject(0);
                        String id = user.getString("id");
                        AtomicBoolean doesStreamerExist = new AtomicBoolean(false);

                        command.getDeviGuild().getStreams().forEach(stream -> {
                            if (stream.getStream().equals(id)) {
                                doesStreamerExist.set(true);
                            }
                        });

                        if (!doesStreamerExist.get()) {
                            sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 214, user.getString("display_name")));
                            return null;
                        }

                        return new AbstractMap.SimpleEntry<>(id, user);
                    } catch (IllegalArgumentException e) {
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 203, "`" + search + "`"));
                        return null;
                    }
                })
                .withCustomVoid(object -> {
                    if (object == null) return;
                    String id = ((Map.Entry<String, JSONObject>)object).getKey();
                    JSONObject user = ((Map.Entry<String, JSONObject>)object).getValue();

                    Stream stream = command.getDeviGuild().getStreams().stream().filter(s -> s.getStream().equals(id)).findAny().orElse(null);

                    if (stream == null) {
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 202, "<https://www.devibot.net/support>"));
                        return;
                    }

                    if (devi.getDatabaseManager().removeFromDatabase("streams", stream.get_id()).wasAcknowledged()) {
                        //not in the streamer map for some reason?!
                        if (!devi.getStreams().containsKey(id)) {
                            devi.changeTwitchSubscriptionStatus(Collections.singleton(id), false);
                        }
                        //this was the only guild that got notifications for that streamer
                        else if (devi.getStreams().get(id).size() == 1) {
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

                        command.getDeviGuild().getStreams().remove(stream);
                        sender.reply(builder.build());
                        devi.getRedisSender().hset("streams#1", id, user.toString());
                        devi.getLogger().log("Removed twitch streamer " + user.getString("display_name") + " (" + id + ") from guild " + command.getEvent().getGuild().getName() + " (" + command.getEvent().getGuild().getId() + ")");
                    } else {
                        sender.reply(Emote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 202, "<https://www.devibot.net/support>"));
                    }
                })
                .build();

        WaitingResponse channel = new WaitingResponseBuilder(devi, command)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 365))
                .setWaiterType(WaitingResponseBuilder.WaiterType.CHANNEL)
                .setSetting(GuildSettings.Settings.TWITCH_CHANNEL)
                .build();

        new WaitingResponseBuilder(devi, command)
                .setWaiterType(WaitingResponseBuilder.WaiterType.SELECTOR)
                .setExpectedInputText(devi.getTranslation(command.getLanguage(), 359, "'" + command.getPrefix() + "streamlist'"))
                .addSelectorOption(devi.getTranslation(command.getLanguage(), 357), addStreamer)
                .addSelectorOption(devi.getTranslation(command.getLanguage(), 358), removeStreamer)
                .addSelectorOption(devi.getTranslation(command.getLanguage(), 356) +
                        " (" + devi.getTranslation(command.getLanguage(), textChannel == null ? 348 : 349, "#" + (textChannel != null ? textChannel.getName() : "??")) + ")", channel)
                .build().handle();
    }

    @Override
    public boolean guildOnly() {
        return true;
    }

    @Override
    public int getDescriptionTranslationID() {
        return 311;
    }

    @Override
    public List<String> getAliases() {
        return null;
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
