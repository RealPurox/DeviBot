package me.purox.devi.core.waiter;

import me.purox.devi.core.Devi;
import me.purox.devi.core.DeviEmote;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class WaitingResponse {

    private WaitingResponseBuilder.WaiterType waiterType;
    private GuildSettings.Settings setting;
    private User executor;
    private MessageChannel channel;
    private Message trigger;
    private Guild guild;
    private String infoText;
    private String typeToCancelText;
    private String cancelledText;
    private String timeOutText;
    private String replyText;
    private String expectedInputText;
    private String tooManyFailures;
    private String invalidInputText;
    private int timeOutInSeconds;
    private HashMap<Integer, Map.Entry<String, WaitingResponse>> waitingResponseHashMap;
    private Devi devi;

    WaitingResponse(Devi devi, WaitingResponseBuilder.WaiterType waiterType, GuildSettings.Settings setting, User executor, MessageChannel channel, Message trigger, Guild guild, String infoText, String typeToCancelText, String cancelledText,
                    String timeOutText, String replyText, String expectedInputText, String tooManyFailures, String invalidInputText, int timeOutInSeconds, HashMap<Integer, Map.Entry<String, WaitingResponse>> waitingResponseHashMap) {

        this.devi = devi;
        this.waiterType = waiterType;
        this.setting = setting;
        this.executor = executor;
        this.channel = channel;
        this.trigger = trigger;
        this.guild = guild;
        this.infoText = infoText;
        this.typeToCancelText = typeToCancelText;
        this.cancelledText = cancelledText;
        this.timeOutText = timeOutText;
        this.replyText = replyText;
        this.expectedInputText = expectedInputText;
        this.tooManyFailures = tooManyFailures;
        this.invalidInputText = invalidInputText;
        this.timeOutInSeconds = timeOutInSeconds;
        this.waitingResponseHashMap = waitingResponseHashMap;
    }

    public void handle() {
        StringBuilder builder = new StringBuilder();

        builder.append(DeviEmote.INFO.get()).append(" | ").append(infoText).append("\n");
        builder.append("```Markdown\n");
        builder.append(expectedInputText);
        builder.append("# ").append(replyText).append(" #").append("\n\n");

        for (int i : waitingResponseHashMap.keySet()) {
            builder.append("[").append(i).append("]: ").append(waitingResponseHashMap.get(i).getKey()).append("\n");
        }
        builder.append("```\n");
        builder.append(typeToCancelText);

        MessageUtils.sendMessageAsync(channel, builder.toString());
        startWaiter(1);
    }

    private void startWaiter(int attempt) {
        int nextAttempt = attempt += 1;
        devi.getResponseWaiter().waitForResponse(guild,
                evt -> {
                    System.out.println(devi.getResponseWaiter().checkUser(evt, trigger.getId(), executor.getId(), channel.getId()));
                    return devi.getResponseWaiter().checkUser(evt, trigger.getId(), executor.getId(), channel.getId());
                },
                response -> {
                    System.out.println("YES");
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        MessageUtils.sendMessageAsync(channel, DeviEmote.SUCCESS.get() + " | " + cancelledText);
                        return;
                    }

                    if (nextAttempt >= 4) {
                        MessageUtils.sendMessageAsync(channel, DeviEmote.ERROR.get() + " | " + tooManyFailures);
                        return;
                    }


                    if (waiterType == WaitingResponseBuilder.WaiterType.SELECTOR) {
                        String input = response.getMessage().getContentRaw().split(" ")[0];

                        int entered;
                        try {
                            entered = Integer.parseInt(input);
                        } catch (NumberFormatException e) {
                            entered = -1;
                        }

                        if (entered < 1 || entered > waitingResponseHashMap.size()) {
                            MessageUtils.sendMessageAsync(channel, DeviEmote.ERROR.get() + " | " + invalidInputText);
                            startWaiter(nextAttempt);
                            return;
                        }

                        WaitingResponse nextWaiter = waitingResponseHashMap.get(entered).getValue();
                        if (nextWaiter == null) {
                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setColor(new Color(179, 0,0));
                            builder.setAuthor("FATAL ERROR");
                            builder.setDescription("Fatal Error: Response waiter for selection response waiter was null");
                            builder.addField("waiterType", String.valueOf(waiterType), false);
                            builder.addField("setting", String.valueOf(setting), false);
                            builder.addField("executor", String.valueOf(executor), false);
                            builder.addField("channel", String.valueOf(this.channel), false);
                            builder.addField("trigger", String.valueOf(trigger), false);
                            builder.addField("guild", String.valueOf(guild), false);
                            builder.addField("infoText", String.valueOf(infoText), false);
                            builder.addField("typeToCancelText", String.valueOf(typeToCancelText), false);
                            builder.addField("cancelledText", String.valueOf(cancelledText), false);
                            builder.addField("timeOutText", String.valueOf(timeOutText), false);
                            builder.addField("replyText", String.valueOf(replyText), false);
                            builder.addField("expectedInputText", String.valueOf(expectedInputText), false);
                            builder.addField("tooManyFailures", String.valueOf(tooManyFailures), false);
                            builder.addField("invalidInputText", String.valueOf(invalidInputText), false);
                            builder.addField("timeOutInSeconds", String.valueOf(timeOutInSeconds), false);
                            builder.addField("waitingResponseHashMap", String.valueOf(waitingResponseHashMap), false);
                            devi.sendMessageToDevelopers(builder.build());
                            MessageUtils.sendMessageAsync(channel, "Jeez " + executor.getAsMention() + ", something went really really really really wrong right here. Our developers have been informed and they will fix this issue as soon as possible.");
                            return;
                        }
                        waitingResponseHashMap.get(entered).getValue().handle();
                    }
                },
                timeOutInSeconds, TimeUnit.SECONDS, () -> MessageUtils.sendMessageAsync(channel, DeviEmote.ERROR.get() + " | " + executor.getAsMention() + ", " + timeOutText));
    }
}
