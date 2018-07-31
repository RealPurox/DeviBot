package me.purox.devi.utils;

import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.client.exceptions.VerificationLevelException;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import net.dv8tion.jda.core.utils.PermissionUtil;

import java.util.function.Consumer;

public class MessageUtils {

    private static Devi devi;
    public MessageUtils(Devi devi) {
        MessageUtils.devi = devi;
    }

    public static boolean deleteMessage(Message message) {
        MessageChannel channel = message.getChannel();
        if (channel instanceof TextChannel) {
            if (((TextChannel) channel).getGuild() != null) {
                if (PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel) channel).getGuild().getSelfMember(), Permission.MESSAGE_MANAGE)) {
                    message.delete().queue();
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean addReactions(Message message, String... emote) {
        MessageChannel channel = message.getChannel();
        if(channel instanceof TextChannel){
            if(((TextChannel)channel).getGuild() != null){
                if(PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel)channel).getGuild().getSelfMember(), Permission.MESSAGE_ADD_REACTION)){
                    for (String e : emote) {
                        message.addReaction(e).queue();
                    }
                    return true;
                }
            }
        } else {
            for (String e : emote) {
                message.addReaction(e).queue();
            }
            return true;
        }
        return false;
    }

    public static void sendPrivateMessageAsync(User user, Object object) {
        try {
            if (user.isBot()) return;

            if (object instanceof MessageEmbed)
                user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage((MessageEmbed) object).queue());
            else if (object instanceof String)
                user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage((String) object).queue());
            else if (object instanceof Message)
                user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage((Message) object).queue());
            else throw new UnsupportedOperationException("Cannot send object " + object + " in messages");

        } catch (IllegalStateException | IllegalArgumentException | UnsupportedOperationException e) {
            e.printStackTrace();
        }
    }

    private static Message sendPrivateMessageSync(User user, Object object) {
        try {
            if (user.isBot()) return null;

            PrivateChannel channel = user.openPrivateChannel().complete();

            if (object instanceof MessageEmbed)
                return channel.sendMessage((MessageEmbed) object).complete();
            else if (object instanceof String)
                return channel.sendMessage((String) object).complete();
            else if (object instanceof Message)
                return channel.sendMessage((Message) object).complete();
            else {
                channel.close().queue();
                throw new UnsupportedOperationException("Cannot send object " + object + " in messages");
            }

        } catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void sendMessageAsync(MessageChannel channel, Object object, Consumer<Message> success) {
        if (channel.getType() == ChannelType.PRIVATE) {
            sendPrivateMessageAsync(((PrivateChannel)channel).getUser(), object);
            return;
        }

        try {

            if (((TextChannel)channel).canTalk()) {
                if (object instanceof MessageEmbed) {
                    if (!PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel)channel).getGuild().getSelfMember(), Permission.MESSAGE_EMBED_LINKS)) {
                        DeviGuild deviGuild = devi.getDeviGuild(((TextChannel) channel).getGuild().getId());
                        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
                        channel.sendMessage(devi.getTranslation(language, 150)).queue(success);
                    }
                    else channel.sendMessage((MessageEmbed) object).queue(success);
                } else if (object instanceof String) {
                    channel.sendMessage((String) object).queue(success);
                } else if (object instanceof Message) {
                    channel.sendMessage((Message) object).queue(success);
                } else {
                    throw new UnsupportedOperationException("Cannot send object " + object + " in messages");
                }
            }

        } catch (VerificationLevelException | InsufficientPermissionException ignored) {
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessageAsync(MessageChannel channel, Object object) {
        sendMessageAsync(channel, object, null);
    }

    public static Message sendMessageSync(MessageChannel channel, Object object) {
        if (channel.getType() == ChannelType.PRIVATE) {
            return sendPrivateMessageSync(((PrivateChannel)channel).getUser(), object);
        }

        try {

            if (((TextChannel)channel).canTalk()) {
                if (object instanceof MessageEmbed) {
                    if (!PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel)channel).getGuild().getSelfMember(), Permission.MESSAGE_EMBED_LINKS)) {
                        DeviGuild deviGuild = devi.getDeviGuild(((TextChannel) channel).getGuild().getId());
                        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
                        return channel.sendMessage(devi.getTranslation(language, 150)).complete();
                    }
                    else return channel.sendMessage((MessageEmbed) object).complete();
                } else if (object instanceof String) {
                    return channel.sendMessage((String) object).complete();
                } else if (object instanceof Message) {
                    return channel.sendMessage((Message) object).complete();
                } else {
                    throw new UnsupportedOperationException("Cannot send object " + object + " in messages");
                }
            }

        } catch (VerificationLevelException | InsufficientPermissionException ignored) {
            return null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
