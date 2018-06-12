package me.purox.devi.commands.guild;

import com.mongodb.client.result.UpdateResult;
import me.purox.devi.commands.handler.Command;
import me.purox.devi.commands.handler.CommandExecutor;
import me.purox.devi.commands.handler.CommandSender;
import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import me.purox.devi.utils.DiscordUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.bson.Document;
import org.json.JSONObject;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TwitchCommandExecutor implements CommandExecutor {

    private Devi devi;

    public TwitchCommandExecutor(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void execute(String[] args, Command command, CommandSender sender) {
        TextChannel textChannel = DiscordUtils.getTextChannel(command.getDeviGuild().getSettings().getStringValue(GuildSettings.Settings.TWITCH_CHANNEL), command.getEvent().getGuild());
        String builder = ":information_source: | " + devi.getTranslation(command.getLanguage(), 355) + "\n\n" +
                "```python\n" +
                devi.getTranslation(command.getLanguage(), 313) + "\n\n" +
                " '1' => " + devi.getTranslation(command.getLanguage(), 357) + "\n" +
                " '2' => " + devi.getTranslation(command.getLanguage(), 358) + "\n" +
                " '3' => " + devi.getTranslation(command.getLanguage(), 356) + " (" + devi.getTranslation(command.getLanguage(), textChannel == null ? 348 : 349, "#" + (textChannel != null ? textChannel.getName() : "??")) + ")\n" +
                devi.getTranslation(command.getLanguage(), 359) +
                "```\n" + devi.getTranslation(command.getLanguage(), 312, "`cancel`");

        sender.reply(builder);
        startWaiter(1, command, sender);
    }

    private void startWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 315));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 316));
                        return;
                    }

                    String input = response.getMessage().getContentRaw().split(" ")[0];

                    int entered;
                    try {
                        entered = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        entered = -1;
                    }

                    switch (entered) {
                        //display
                        case 1:
                            String addResponse = ":information_source: | " + devi.getTranslation(command.getLanguage(), 355) + " -> " + devi.getTranslation(command.getLanguage(), 360) + "\n\n" +
                                    "```python\n" +
                                    devi.getTranslation(command.getLanguage(), 363) +
                                    "```\n" + devi.getTranslation(command.getLanguage(), 312, "`cancel`");
                            sender.reply(addResponse);
                            startAddWaiter(1, command, sender);
                            break;
                        //add
                        case 2:
                            String removeResponse = ":information_source: | " + devi.getTranslation(command.getLanguage(), 355) + " -> " + devi.getTranslation(command.getLanguage(), 361) + "\n\n" +
                                    "```python\n" +
                                    devi.getTranslation(command.getLanguage(), 364) +
                                    "```\n" + devi.getTranslation(command.getLanguage(), 312, "`cancel`");
                            sender.reply(removeResponse);
                            startRemoveWaiter(1, command, sender);
                            break;
                        //remove
                        case 3:
                            String channelResponse = ":information_source: | " + devi.getTranslation(command.getLanguage(), 355) + " -> " + devi.getTranslation(command.getLanguage(), 362) + "\n\n" +
                                    "```python\n" +
                                    devi.getTranslation(command.getLanguage(), 365) +
                                    "```\n" + devi.getTranslation(command.getLanguage(), 312, "`cancel`");
                            sender.reply(channelResponse);
                            startChannelWaiter(1, command, sender);
                            break;
                        default:
                            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 265));
                            startWaiter(nextAttempt, command, sender);
                            break;
                    }
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 314)));
    }

    private void startAddWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 315));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 317));
                        return;
                    }

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
                            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 366));
                            startAddWaiter(nextAttempt, command, sender);
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
                            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 204, user.getString("display_name")));
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
                                builder.appendDescription(devi.getTranslation(command.getLanguage(), 206, "`" + command.getPrefix() + "twitch`"));
                            else
                                builder.appendDescription(devi.getTranslation(command.getLanguage(), 207, textChannel.getAsMention()));

                            command.getDeviGuild().getStreams().add(document);
                            sender.reply(builder.build());
                            devi.getRedisSender().hset("streams#1", id, user.toString());
                        } else {
                            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 202, "<https://www.devibot.net/support>"));
                        }
                    } catch (IllegalArgumentException e) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 203, "`" + search + "`"));
                    }
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 314)));
    }

    private void startRemoveWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 315));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 317));
                        return;
                    }

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
                            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 366));
                            startRemoveWaiter(nextAttempt, command, sender);
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
                            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 214, user.getString("display_name")));
                            return;
                        }

                        Document document = command.getDeviGuild().getStreams().stream().filter(doc -> doc.getString("stream").equals(id)).findAny().orElse(null);

                        if (document == null) {
                            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 202, "<https://www.devibot.net/support>"));
                            return;
                        }

                        if (devi.getDatabaseManager().removeFromDatabase("streams", document.getString("_id")).wasAcknowledged()) {
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

                            command.getDeviGuild().getStreams().remove(document);
                            sender.reply(builder.build());
                            devi.getRedisSender().hset("streams#1", id, user.toString());
                        } else {
                            sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 202, "<https://www.devibot.net/support>"));
                        }
                    } catch (IllegalArgumentException e) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 203, "`" + search + "`"));
                    }
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 314)));
    }

    private void startChannelWaiter(int attempt, Command command, CommandSender sender) {
        int nextAttempt = attempt += 1;
        MessageReceivedEvent event = command.getEvent();
        devi.getResponseWaiter().waitForResponse(event.getGuild(),
                evt -> devi.getResponseWaiter().checkUser(evt, event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId()),
                response -> {
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 315));
                        return;
                    }

                    if (nextAttempt >= 4) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 367));
                        return;
                    }

                    String input = response.getMessage().getContentRaw();
                    TextChannel textChannel = DiscordUtils.getTextChannel(input, command.getEvent().getGuild());

                    if (textChannel == null) {
                        sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 331));
                        startChannelWaiter(nextAttempt, command, sender);
                        return;
                    }

                    command.getDeviGuild().getSettings().setStringValue(GuildSettings.Settings.TWITCH_CHANNEL, textChannel.getId());
                    command.getDeviGuild().saveSettings();
                    sender.reply(DeviEmote.SUCCESS.get() + " | " + devi.getTranslation(command.getLanguage(), 368, textChannel.getAsMention()));
                },
                15, TimeUnit.SECONDS, () -> sender.reply(DeviEmote.ERROR.get() + " | " + devi.getTranslation(command.getLanguage(), 309)) );
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
}
