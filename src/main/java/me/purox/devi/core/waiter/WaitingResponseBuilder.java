package me.purox.devi.core.waiter;

import me.purox.devi.commands.handler.Command;
import me.purox.devi.core.Devi;
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
    private int timeOutInSeconds;
    private HashMap<Integer, Map.Entry<String, WaitingResponse>> waitingResponseHashMap;
    private Devi devi;
    private GuildSettings.Settings setting;

    public WaitingResponseBuilder(Devi devi, Command command) {
        this.executor = command.getEvent().getAuthor();
        this.channel = command.getEvent().getChannel();
        this.trigger = command.getEvent().getMessage();
        this.guild = command.getEvent().getGuild();
        this.devi = devi;

        //default texts
        this.waitingResponseHashMap = new HashMap<>();
        this.infoText = "You're currently editing your settings";
        this.typeToCancelText = "You can cancel editing your settings by typing 'cancel'";
        this.cancelledText = "The settings selection has been cancelled.";
        this.timeOutText = "You took to long to respond to my message.";
        this.replyText = "Reply with one of the options listed below to edit your settings";
        this.tooManyFailures = "You've failed to enter a valid response three times in a row. " + cancelledText;
        this.invalidInputText = "The responses you've provided is invalid. Please try again.";
        this.expectedInputText = "";
        this.timeOutInSeconds = 30;
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

    public WaitingResponseBuilder addSelectorOption(String message, WaitingResponse waitingResponse) {
        int next = waitingResponseHashMap.size() + 1;
        waitingResponseHashMap.put(next, new AbstractMap.SimpleEntry<>(message, waitingResponse));
        return this;
    }

    public WaitingResponse build() {
        Checks.notNull(executor, "executor");
        Checks.notNull(channel, "channel");
        Checks.notNull(trigger, "trigger");
        Checks.notNull(guild, "guild");
        Checks.notNull(waiterType, "waiterType");
        if(waiterType != WaiterType.SELECTOR) Checks.notNull(setting, "setting");
        return new WaitingResponse(devi, waiterType, setting, executor, channel, trigger, guild, infoText, typeToCancelText, cancelledText, timeOutText, replyText, expectedInputText,
                tooManyFailures, invalidInputText, timeOutInSeconds, waitingResponseHashMap);
    }


    public enum WaiterType {
        SELECTOR, CHANNEL, ROLE, USER, LANGUAGE, BOOLEAN
    }
}