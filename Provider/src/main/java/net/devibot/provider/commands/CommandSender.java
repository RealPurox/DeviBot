package net.devibot.provider.commands;

import net.devibot.provider.utils.MessageBuilder;
import net.devibot.provider.utils.MessageUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.List;
import java.util.function.Consumer;

public class CommandSender implements User {

    private User user;
    private ICommand.Command command;

    public CommandSender (User user, ICommand.Command command) {
        this.user = user;
        this.command = command;
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
        return command.getGuild().getMember(this);
    }

    @Deprecated
    public void reply(Object message) {
        reply(message, null, null);
    }

    @Deprecated
    public void reply(Object message, Consumer<? super Message> success) {
        reply(message, success, null);
    }

    @Deprecated
    public void reply(Object message, Consumer<? super Message> success, Consumer<? super Throwable> failure) {
        MessageUtils.sendMessage(command.getChannel(), message, success, failure);
    }

    public MessageBuilder errorMessage() {
        return new MessageBuilder(MessageBuilder.FAILURE_TEMPLATE).setChannel(command.getChannel()).setLanguage(command.getLanguage());
    }

    public MessageBuilder successMessage() {
        return new MessageBuilder(MessageBuilder.SUCCESS_TEMPLATE).setChannel(command.getChannel()).setLanguage(command.getLanguage());
    }

    public MessageBuilder infoMessage() {
        return new MessageBuilder(MessageBuilder.INFO_TEMPLATE).setChannel(command.getChannel()).setLanguage(command.getLanguage());
    }

    public MessageBuilder message() {
        return new MessageBuilder().setChannel(command.getChannel()).setLanguage(command.getLanguage());
    }

}
