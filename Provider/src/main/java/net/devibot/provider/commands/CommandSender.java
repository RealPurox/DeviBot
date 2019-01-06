package net.devibot.provider.commands;

import net.devibot.provider.utils.MessageUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.List;
import java.util.function.Consumer;

public class CommandSender implements User {

    private User user;
    private MessageReceivedEvent event;

    public CommandSender (User user, MessageReceivedEvent event) {
        this.user = user;
        this.event = event;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getDiscriminator() {
        return user.getDiscriminator();
    }

    @Override
    public String getAvatarId() {
        return user.getAvatarId();
    }

    @Override
    public String getAvatarUrl() {
        return user.getAvatarUrl();
    }

    @Override
    public String getDefaultAvatarId() {
        return user.getDefaultAvatarId();
    }

    @Override
    public String getDefaultAvatarUrl() {
        return user.getDefaultAvatarUrl();
    }

    @Override
    public String getEffectiveAvatarUrl() {
        return user.getEffectiveAvatarUrl();
    }

    @Override
    public boolean hasPrivateChannel() {
        return user.hasPrivateChannel();
    }

    @Override
    public RestAction<PrivateChannel> openPrivateChannel() {
        return user.openPrivateChannel();
    }

    @Override
    public List<Guild> getMutualGuilds() {
        return user.getMutualGuilds();
    }

    @Override
    public boolean isBot() {
        return user.isBot();
    }

    @Override
    public JDA getJDA() {
        return user.getJDA();
    }

    @Override
    public boolean isFake() {
        return user.isFake();
    }

    @Override
    public String getAsMention() {
        return user.getAsMention();
    }

    @Override
    public long getIdLong() {
        return user.getIdLong();
    }

    public Member getMember() {
        return event.getGuild().getMember(this);
    }

    public void reply(Object message) {
        reply(message, null, null);
    }

    public void reply(Object message, Consumer<? super Message> success) {
        reply(message, null, null);
    }

    public void reply(Object message, Consumer<? super Message> success, Consumer<? super Throwable> failure) {
        MessageUtils.sendMessage(event.getChannel(), message, success, failure);
    }


}
