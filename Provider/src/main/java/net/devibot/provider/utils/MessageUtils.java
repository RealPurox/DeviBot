package net.devibot.provider.utils;

import net.devibot.core.entities.DeviGuild;
import net.devibot.provider.Provider;
import net.devibot.provider.entities.Language;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class MessageUtils {

    private static Logger logger = LoggerFactory.getLogger(MessageUtils.class);

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

                }
            } else if (object instanceof String || object instanceof Message) {

            } else {

            }

        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static void sendPrivateMessage(User user, Object object, Consumer<? super Message> success, Consumer<? super Throwable> failure) {

    }
}
