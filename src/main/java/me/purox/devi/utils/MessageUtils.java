package me.purox.devi.utils;

import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.utils.PermissionUtil;

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

    public static Message sendMessage(MessageChannel channel, MessageEmbed embed){
        return sendObjectMessage(channel, embed);
    }

    public static Message sendMessage(MessageChannel channel, String string){
        return sendObjectMessage(channel, string);
    }

    public static Message sendPrivateMessage(User user, MessageEmbed message) {
        try {
            PrivateChannel channel = user.openPrivateChannel().complete();
            return sendObjectMessage(channel, message);
        } catch (ErrorResponseException e){
            return null;
        }
    }

    public static Message sendPrivateMessage(User user, String message) {
        try {
            PrivateChannel channel = user.openPrivateChannel().complete();
            return sendObjectMessage(channel, message);
        } catch (ErrorResponseException e){
            return null;
        }
    }

    private static Message sendObjectMessage(MessageChannel channel, Object object){
        if(!(object instanceof String) && !(object instanceof MessageEmbed)){
            return null;
        }
        String message = null;
        MessageEmbed embed = null;
        if(object instanceof String) message = (String) object;
        else embed = (MessageEmbed) object;

        if(channel instanceof TextChannel){
            if(((TextChannel)channel).getGuild() != null){
                if(PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel)channel).getGuild().getSelfMember(), Permission.MESSAGE_WRITE)){
                    if (message == null) {
                        if (!PermissionUtil.checkPermission((TextChannel) channel, ((TextChannel)channel).getGuild().getSelfMember(), Permission.MESSAGE_EMBED_LINKS)) {
                            DeviGuild deviGuild = devi.getDeviGuild(((TextChannel) channel).getGuild().getId());
                            Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));
                            return MessageUtils.sendMessage(channel, ":warning: **" + devi.getTranslation(language, 150) + "**");
                        }
                        return channel.sendMessage(embed).complete();
                    }
                    else return channel.sendMessage(message).complete();
                }
            }
        } else {
            if (message == null) return channel.sendMessage(embed).complete();
            else return channel.sendMessage(message).complete();
        }
        return null;
    }
}
