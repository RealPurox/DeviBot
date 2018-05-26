package me.purox.devi.commands.handler;

import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.requests.RestAction;

import java.util.List;

public class CommandSender implements User, ConsoleCommandSender {

    private User userCommandSender;
    private ConsoleCommandSender consoleCommandSender;
    private MessageReceivedEvent event;

    public CommandSender (User user, MessageReceivedEvent event) {
        this.userCommandSender = user;
        this.event = event;
    }

    public CommandSender (ConsoleCommandSender consoleCommandSender, MessageReceivedEvent event) {
        this.consoleCommandSender = consoleCommandSender;
        this.event = event;
    }

    @Override
    public String getName() {
        return consoleCommandSender == null ? userCommandSender.getName(): consoleCommandSender.getName();
    }

    @Override
    public String getDiscriminator() {
        return consoleCommandSender == null ? userCommandSender.getDiscriminator(): consoleCommandSender.getDiscriminator();
    }

    @Override
    public String getAvatarId() {
        return consoleCommandSender == null ? userCommandSender.getAvatarId(): consoleCommandSender.getAvatarId();
    }

    @Override
    public String getAvatarUrl() {
        return consoleCommandSender == null ? userCommandSender.getAvatarUrl(): consoleCommandSender.getAvatarUrl();
    }

    @Override
    public String getDefaultAvatarId() {
        return consoleCommandSender == null ? userCommandSender.getDefaultAvatarId(): consoleCommandSender.getDefaultAvatarId();
    }

    @Override
    public String getDefaultAvatarUrl() {
        return consoleCommandSender == null ? userCommandSender.getDefaultAvatarUrl(): consoleCommandSender.getDefaultAvatarUrl();
    }

    @Override
    public String getEffectiveAvatarUrl() {
        return consoleCommandSender == null ? userCommandSender.getEffectiveAvatarUrl(): consoleCommandSender.getEffectiveAvatarUrl();
    }

    @Override
    public boolean hasPrivateChannel() {
        return consoleCommandSender == null ? userCommandSender.hasPrivateChannel(): consoleCommandSender.hasPrivateChannel();
    }

    @Override
    public RestAction<PrivateChannel> openPrivateChannel() {
        return consoleCommandSender == null ? userCommandSender.openPrivateChannel(): consoleCommandSender.openPrivateChannel();
    }

    @Override
    public List<Guild> getMutualGuilds() {
        return consoleCommandSender == null ? userCommandSender.getMutualGuilds(): consoleCommandSender.getMutualGuilds();
    }

    @Override
    public boolean isBot() {
        return consoleCommandSender == null ? userCommandSender.isBot(): consoleCommandSender.isBot();
    }

    @Override
    public JDA getJDA() {
        return consoleCommandSender == null ? userCommandSender.getJDA(): consoleCommandSender.getJDA();
    }

    @Override
    public boolean isFake() {
        return consoleCommandSender == null ? userCommandSender.isFake(): consoleCommandSender.isFake();
    }

    @Override
    public String getAsMention() {
        return consoleCommandSender == null ? userCommandSender.getAsMention(): consoleCommandSender.getAsMention();
    }

    @Override
    public long getIdLong() {
        return consoleCommandSender == null ? userCommandSender.getIdLong(): consoleCommandSender.getIdLong();
    }

    public boolean isConsoleCommandSender() {
        return userCommandSender == null;
    }

    public boolean isUserCommandSender() {
        return consoleCommandSender == null;
    }

    public void reply(String message) {
        if (userCommandSender == null) {
            System.out.println(message);
        } else {
            MessageUtils.sendMessageAsync(event.getChannel(), message);
        }
    }

    public void reply(MessageEmbed embed) {
        if (userCommandSender == null) {
            StringBuilder message = new StringBuilder();
            message.append(embed.getAuthor() == null ? "" : embed.getAuthor().getName() + "\n");
            message.append(embed.getTitle() == null ? "" : embed.getTitle() + "\n");
            message.append(embed.getDescription() == null ? "" : embed.getDescription() + "\n");

            for (MessageEmbed.Field f : embed.getFields()) {
                message.append("\n").append(f.getName()).append(":\n").append(f.getValue()).append("\n");
            }

            message.append(embed.getFooter() == null ? "" : embed.getFooter().getText());
            System.out.println(message);
        } else {
            MessageUtils.sendMessageAsync(event.getChannel(), embed);
        }
    }
}
