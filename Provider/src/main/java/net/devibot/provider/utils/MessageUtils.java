package net.devibot.provider.utils;

import net.devibot.core.entities.DeviGuild;
import net.devibot.provider.Provider;
import net.devibot.provider.entities.Emote;
import net.devibot.provider.entities.Language;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class MessageUtils {

    private static Logger logger = LoggerFactory.getLogger(MessageUtils.class);

    public static void sendMessage(MessageChannel channel, Object object) {
        sendMessage(channel, object, null, null);
    }

    public static void sendMessage(MessageChannel channel, Object object, Consumer<? super Message> success) {
        sendMessage(channel, object, success, null);
    }

    public static void sendMessage(MessageChannel channel, Object object, Consumer<? super Message> success, Consumer<? super Throwable> failure) {
        if (channel.getType() == ChannelType.PRIVATE) {
            sendPrivateMessage(((PrivateChannel)channel).getUser(), object, success, failure);
            return;
        }

        if (!(channel instanceof TextChannel)) return;
        if (!((TextChannel) channel).canTalk()) return;

        try {

            if (object instanceof MessageEmbed) {
                if (!PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(), Permission.MESSAGE_EMBED_LINKS)) {
                    DeviGuild deviGuild = Provider.getInstance().getCacheManager().getDeviGuildCache().getDeviGuild(((TextChannel) channel).getGuild().getId());
                    Language language = Language.getLanguage(deviGuild.getLanguage());
                    channel.sendMessage(Emote.ERROR + " | " + Translator.getTranslation(language, 150)).queue(success, failure);
                } else channel.sendMessage((MessageEmbed) object).queue(success, failure);
            } else if (object instanceof String) {
                String message = (String) object;
                if (message.length() >= 2000)
                    message = message.substring(message.length() - 2000);
                channel.sendMessage(message).queue(success, failure);
            } else if (object instanceof Message) {
                channel.sendMessage((Message) object).queue(success, failure);
            } else {
                if (object.toString().length() >= 2000)
                    object = object.toString().substring(object.toString().length() - 2000);
                channel.sendMessage(object.toString()).queue(success, failure);
            }

        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static void sendPrivateMessage(User user, Object object) {
        sendPrivateMessage(user, object, null, null);
    }

    public static void sendPrivateMessage(User user, Object object, Consumer<? super Message> success) {
        sendPrivateMessage(user, object, success, null);
    }

    public static void sendPrivateMessage(User user, Object object, Consumer<? super Message> success, Consumer<? super Throwable> failure) {
        if (user.isBot()) return;

        try {

            if (object instanceof MessageEmbed) {
                user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage((MessageEmbed) object).queue(done -> {
                    if (success != null) success.accept(done);
                    privateChannel.close().queue();
                    }, failure), failure);
            } else if (object instanceof String) {
                String message = (String) object;
                if (message.length() >= 2000)
                    message = message.substring(message.length() - 2000);
                String finalMessage = message;
                user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(finalMessage).queue(done -> {
                    success.accept(done);
                    privateChannel.close().queue();
                }, failure), failure);
            } else if (object instanceof  Message) {
                user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage((Message) object).queue(done -> {
                    if (success != null) success.accept(done);
                    privateChannel.close().queue();
                }, failure), failure);
            } else {
                Object message = object;
                if (message.toString().length() >= 2000)
                    message = object.toString().substring(message.toString().length() - 2000);
                Object finalMessage = message;
                user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(finalMessage.toString()).queue(done -> {
                    if (success != null) success.accept(done);
                    privateChannel.close().queue();
                }, failure), failure);
            }

        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
