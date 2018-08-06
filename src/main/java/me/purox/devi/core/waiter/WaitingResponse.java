package me.purox.devi.core.waiter;

import me.purox.devi.core.Devi;
import me.purox.devi.core.Emote;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.DiscordUtils;
import me.purox.devi.utils.JavaUtils;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.Checks;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private String booleanActivatedText;
    private String booleanDeactivatedText;
    private String stringChangedText;
    private int timeOutInSeconds;
    private HashMap<Integer, Map.Entry<String, WaitingResponse>> waitingResponseHashMap;
    private HashMap<Integer, Map.Entry<String, WaiterVoid>> waitingVoidResponseHashMap;
    private Devi devi;

    private WaiterCheck customCheck;
    private String customCheckFailureText;
    private boolean tryAgainAfterCustomCheckFail;

    private WaiterVoid customVoid;



    WaitingResponse(Devi devi, WaitingResponseBuilder.WaiterType waiterType, GuildSettings.Settings setting, User executor, MessageChannel channel, Message trigger, Guild guild, String infoText, String typeToCancelText, String cancelledText,
                    String timeOutText, String replyText, String expectedInputText, String tooManyFailures, String invalidInputText, String booleanActivatedText,
                    String booleanDeactivatedText, String stringChangedText, int timeOutInSeconds, HashMap<Integer, Map.Entry<String, WaitingResponse>> waitingResponseHashMap,
                    HashMap<Integer, Map.Entry<String, WaiterVoid>> waitingVoidResponseHashMap, WaiterCheck customCheck, String customCheckFailureText, boolean tryAgainAfterCustomCheckFail,
                    WaiterVoid customVoid) {

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
        this.booleanActivatedText = booleanActivatedText;
        this.booleanDeactivatedText = booleanDeactivatedText;
        this.stringChangedText = stringChangedText;

        this.timeOutInSeconds = timeOutInSeconds;
        this.waitingResponseHashMap = waitingResponseHashMap;
        this.waitingVoidResponseHashMap = waitingVoidResponseHashMap;

        this.customCheck = customCheck;
        this.customCheckFailureText = customCheckFailureText;
        this.tryAgainAfterCustomCheckFail = tryAgainAfterCustomCheckFail;
        this.customVoid = customVoid;
    }

    public void handle() {
        StringBuilder builder = new StringBuilder();

        builder.append(Emote.INFO.get()).append(" | ").append(infoText).append("\n\n");
        builder.append("```Markdown\n");
        if(!replyText.equals("")) builder.append("# ").append(replyText).append("\n\n");

        for (int i = 1; i != waitingResponseHashMap.size() + waitingVoidResponseHashMap.size() + 1; i++) {
            if (waitingResponseHashMap.containsKey(i))
                builder.append("[").append(i).append("]: ").append(waitingResponseHashMap.get(i).getKey()).append("\n");
            if (waitingVoidResponseHashMap.containsKey(i))
                builder.append("[").append(i).append("]: ").append(waitingVoidResponseHashMap.get(i).getKey()).append("\n");
        }
        if(!expectedInputText.equals("")) builder.append("\n# ").append(expectedInputText).append("\n\n");
        builder.append("```\n");
        builder.append(typeToCancelText);

        MessageUtils.sendMessageAsync(channel, builder.toString());
        startWaiter(1);
    }

    private void startWaiter(int attempt) {
        int nextAttempt = attempt += 1;
        devi.getResponseWaiter().waitForResponse(guild,
                evt -> devi.getResponseWaiter().checkUser(evt, trigger.getId(), executor.getId(), channel.getId()),
                response -> {
                    //cancel waiter
                    if (response.getMessage().getContentRaw().toLowerCase().startsWith("cancel")) {
                        MessageUtils.sendMessageAsync(channel, Emote.SUCCESS.get() + " | " + cancelledText);
                        return;
                    }

                    //It's a void or ResponseWaiter selector
                    if (waiterType == WaitingResponseBuilder.WaiterType.SELECTOR) {
                        String input = response.getMessage().getContentRaw().split(" ")[0];

                        int entered;
                        try {
                            entered = Integer.parseInt(input);
                        } catch (NumberFormatException e) {
                            entered = -1;
                        }

                        //not a valid number
                        if (!waitingResponseHashMap.containsKey(entered) && !waitingVoidResponseHashMap.containsKey(entered)) {
                            if (nextAttempt >= 4) {
                                MessageUtils.sendMessageAsync(channel, Emote.ERROR.get() + " | " + tooManyFailures);
                                return;
                            } else {
                                MessageUtils.sendMessageAsync(channel, Emote.ERROR.get() + " | " + invalidInputText);
                                startWaiter(nextAttempt);
                            }
                            return;
                        }

                        //next waiter
                        if (waitingResponseHashMap.containsKey(entered)) {
                            WaitingResponse nextWaiter = waitingResponseHashMap.get(entered).getValue();
                            Checks.notNull(nextWaiter, "nexWaiter");
                            nextWaiter.handle();
                        //next void
                        } else if (waitingVoidResponseHashMap.containsKey(entered)) {
                            WaiterVoid waiterVoid = waitingVoidResponseHashMap.get(entered).getValue();
                            Checks.notNull(waiterVoid, "waiterVoid");
                            waiterVoid.run(null);
                        } else
                            //should never happen
                            throw new IllegalStateException("Something went wrong here");
                    //not a selector
                    } else {
                        //custom check
                        if (customCheck != null) {
                            Object check = customCheck.check(response);
                            if (check == null) {
                                if (tryAgainAfterCustomCheckFail) {
                                    if (nextAttempt >= 4) {
                                        MessageUtils.sendMessageAsync(channel, Emote.ERROR.get() + " | " + tooManyFailures);
                                        return;
                                    } else {
                                        if (customCheckFailureText != null)
                                            MessageUtils.sendMessageAsync(channel, Emote.ERROR.get() + " | " + customCheckFailureText);
                                        startWaiter(nextAttempt);
                                    }
                                }
                            }
                            //custom void, mostly used if stuff is saved in a different DB collection.
                            if (customVoid != null) {
                                customVoid.run(check);
                                return;
                            }
                        }

                        DeviGuild deviGuild = devi.getDeviGuild(guild.getId());
                        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

                        String input = response.getMessage().getContentRaw();
                        String firstArgument = input.split(" ")[0];

                        Object object = null;
                        String changedTo = "";

                        switch (waiterType) {
                            case CHANNEL:
                                TextChannel channel = DiscordUtils.getTextChannel(input, guild);
                                if (channel == null) channel = DiscordUtils.getTextChannel(firstArgument, guild);
                                if (channel == null) break;
                                object = channel.getId();
                                changedTo = channel.getAsMention();
                                break;
                            case ROLE:
                                Role role = DiscordUtils.getRole(input, guild);
                                if (role == null) role = DiscordUtils.getRole(firstArgument, guild);
                                if (role == null) break;
                                object = role.getId();
                                changedTo = role.getName();
                                break;
                            case USER:
                                User user = DiscordUtils.getUser(input, guild);
                                if (user == null) user = DiscordUtils.getUser(firstArgument , guild);
                                if (user == null) break;
                                object = user.getId();
                                changedTo = user.getName() + "#" + user.getDiscriminator();
                                break;
                            case LANGUAGE:
                                Language lang = Language.getLanguage(firstArgument);
                                if (lang == null) break;
                                object = lang.getName().toLowerCase();
                                changedTo = lang.getName();
                                break;
                            case BOOLEAN:
                                Boolean bool = JavaUtils.getBoolean(firstArgument);
                                if (bool == null) break;
                                object = bool;
                                changedTo = JavaUtils.makeBooleanBeautiful(bool);
                                break;
                            case TEXT:
                                object = input.toLowerCase();
                                changedTo = input.toLowerCase();
                                break;
                            default:
                                changedTo = "[??? Something Went Wrong ???]";
                                break;
                        }

                        if (object == null) {
                            if (nextAttempt >= 4) {
                                MessageUtils.sendMessageAsync(channel, Emote.ERROR.get() + " | " + tooManyFailures);
                                return;
                            } else {
                                MessageUtils.sendMessageAsync(channel, Emote.ERROR.get() + " | " + devi.getTranslation(language, 413));
                                startWaiter(nextAttempt);
                            }
                            return;
                        }

                        if (setting.isBooleanValue() && object instanceof Boolean) {
                            deviGuild.getSettings().setBooleanValue(setting, (Boolean) object);
                            String message = (Boolean) object ? booleanActivatedText : booleanDeactivatedText;
                            MessageUtils.sendMessageAsync(channel, Emote.SUCCESS.get() + " | " + message.replace("{0}", setting.getName()));
                        }
                        else if (setting.isStringValue() && object instanceof String) {
                            deviGuild.getSettings().setStringValue(setting, (String) object);
                            MessageUtils.sendMessageAsync(channel, Emote.SUCCESS.get() + " | " + stringChangedText.replace("{0}", setting.getName()).replace("{1}", changedTo));
                        }
                    }
                },
                timeOutInSeconds, TimeUnit.SECONDS, () -> MessageUtils.sendMessageAsync(channel, Emote.ERROR.get() + " | " + executor.getName() + ", " + timeOutText));
    }
}
