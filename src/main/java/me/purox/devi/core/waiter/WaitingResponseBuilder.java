package me.purox.devi.core.waiter;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.Checks;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class WaitingResponseBuilder {

    private Devi devi;
    private GuildSettings.Settings setting;

    private WaiterType waiterType;
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

    private HashMap<Integer, Map.Entry<String, WaitingResponse>> waitingResponseHashMap;
    private HashMap<Integer, Map.Entry<String, WaiterVoid>> waitingVoidResponseHashMap;

    private int lastUsedSelectorIndex = 1;
    private int timeOutInSeconds;

    private WaiterCheck customCheck;
    private String customCheckFailureText;
    private boolean tryAgainAfterCustomCheckFail;

    private WaiterVoid customVoid;

    public WaitingResponseBuilder(Devi devi, Command command) {
        this.executor = command.getEvent().getAuthor();
        this.channel = command.getEvent().getChannel();
        this.trigger = command.getEvent().getMessage();
        this.guild = command.getEvent().getGuild();
        this.devi = devi;

        Language language = Language.getLanguage(devi.getDeviGuild(guild.getId()).getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

        //default texts
        this.waitingResponseHashMap = new HashMap<>();
        this.waitingVoidResponseHashMap = new HashMap<>();
        this.infoText = devi.getTranslation(language, 407);
        this.typeToCancelText = devi.getTranslation(language, 408);
        this.cancelledText = devi.getTranslation(language, 409);
        this.timeOutText = devi.getTranslation(language, 410);
        this.replyText = devi.getTranslation(language, 411);
        this.tooManyFailures = devi.getTranslation(language, 412) + " " + cancelledText;
        this.invalidInputText = devi.getTranslation(language, 413);
        this.expectedInputText = "";
        this.booleanActivatedText = devi.getTranslation(language, 414);
        this.booleanDeactivatedText = devi.getTranslation(language, 415);
        this.stringChangedText = devi.getTranslation(language, 416);
        this.timeOutInSeconds = 30;
    }

    public WaitingResponseBuilder withCustomCheck(WaiterCheck customCheck) {
        this.customCheck = customCheck;
        return this;
    }

    public WaitingResponseBuilder withCustomVoid(WaiterVoid waiterVoid) {
        this.customVoid = waiterVoid;
        return this;
    }

    public WaitingResponseBuilder setCustomCheckFailureText(String customCheckFailureText) {
        this.customCheckFailureText = customCheckFailureText;
        return this;
    }

    public WaitingResponseBuilder setTryAgainAfterCustomCheckFail(boolean tryAgainAfterCustomCheckFail) {
        this.tryAgainAfterCustomCheckFail = tryAgainAfterCustomCheckFail;
        return this;
    }

    public WaitingResponseBuilder setWaiterType(WaiterType waiterType) {
        this.waiterType = waiterType;
        return this;
    }

    public WaitingResponseBuilder setInfoText(String infoText) {
        this.infoText = infoText;
        return this;
    }

    public WaitingResponseBuilder setTypeToCancelText(String typeToCancelText) {
        this.typeToCancelText = typeToCancelText;
        return this;
    }

    public WaitingResponseBuilder setCancelledText(String cancelledText) {
        this.cancelledText = cancelledText;
        return this;
    }

    public WaitingResponseBuilder setTimeOutText(String timeOutText) {
        this.timeOutText = timeOutText;
        return this;
    }

    public WaitingResponseBuilder setReplyText(String replyText) {
        this.replyText = replyText;
        return this;
    }

    public WaitingResponseBuilder setExpectedInputText(String expectedInputText) {
        this.expectedInputText = expectedInputText;
        return this;
    }

    public WaitingResponseBuilder setTooManyFailures(String tooManyFailures) {
        this.tooManyFailures = tooManyFailures;
        return this;
    }

    public WaitingResponseBuilder setInvalidInputText(String invalidInputText) {
        this.invalidInputText = invalidInputText;
        return this;
    }

    public WaitingResponseBuilder setTimeOutInSeconds(int timeOutInSeconds) {
        this.timeOutInSeconds = timeOutInSeconds;
        return this;
    }

    public WaitingResponseBuilder setBooleanActivatedText(String booleanActivatedText) {
        this.booleanActivatedText = booleanActivatedText;
        return this;
    }

    public WaitingResponseBuilder setBooleanDeactivatedText(String booleanDeactivatedText) {
        this.booleanDeactivatedText = booleanDeactivatedText;
        return this;
    }

    public WaitingResponseBuilder setSetting(GuildSettings.Settings setting) {
        this.setting = setting;
        return this;
    }

    public WaitingResponseBuilder addSelectorOption(String message, WaitingResponse waitingResponse) {
        int next = lastUsedSelectorIndex ++ ;
        waitingResponseHashMap.put(next, new AbstractMap.SimpleEntry<>(message, waitingResponse));
        return this;
    }

    public WaitingResponseBuilder addVoidSelectorOption(String message, WaiterVoid waiterVoid) {
        int next = lastUsedSelectorIndex ++;
        waitingVoidResponseHashMap.put(next, new AbstractMap.SimpleEntry<>(message, waiterVoid));
        return this;
    }

    public WaitingResponse build() {
        Checks.notNull(executor, "executor");
        Checks.notNull(channel, "channel");
        Checks.notNull(trigger, "trigger");
        Checks.notNull(guild, "guild");
        Checks.notNull(waiterType, "waiterType");
        if(waiterType != WaiterType.SELECTOR && waiterType != WaiterType.CUSTOM) {
            this.replyText = "";
            Checks.notNull(setting, "setting");
        }
        return new WaitingResponse(devi, waiterType, setting, executor, channel, trigger, guild, infoText, typeToCancelText, cancelledText, timeOutText, replyText, expectedInputText,
                tooManyFailures, invalidInputText, booleanActivatedText, booleanDeactivatedText, stringChangedText, timeOutInSeconds, waitingResponseHashMap,
                waitingVoidResponseHashMap, customCheck, customCheckFailureText, tryAgainAfterCustomCheckFail, customVoid);
    }


    public enum WaiterType {
        SELECTOR, CHANNEL, ROLE, USER, LANGUAGE, BOOLEAN, TEXT, CUSTOM
    }
}