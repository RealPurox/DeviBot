package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.entities.supportchat.SupportChat;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


public class SupportChatListener extends ListenerAdapter {

    private Devi devi;

    public SupportChatListener(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
         for (SupportChat supportChat : devi.getSupportChats()) {
            if (supportChat.getChannel().equals(event.getChannel().getId()) &&  supportChat.getStaff().equalsIgnoreCase(event.getAuthor().getId())) {
                MessageUtils.sendPrivateMessageAsync(devi.getShardManager().getUserById(supportChat.getUser()), getEmbed(event.getMessage(), event.getAuthor()), msg -> event.getMessage().addReaction("✅").queue());
                break;
            }
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        for (SupportChat supportChat : devi.getSupportChats()) {
            if (supportChat.getUser().equals(event.getAuthor().getId())) {
                MessageUtils.sendMessageAsync(devi.getStaffGuild().getTextChannelById(supportChat.getChannel()), getEmbed(event.getMessage(), event.getAuthor()), msg -> event.getMessage().addReaction("✅").queue());
                break;
            }
        }
    }

    private MessageEmbed getEmbed(Message message, User author) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(devi.getColor());
        builder.setAuthor("Support Chat");
        builder.setDescription(message.getContentRaw());
        for (Message.Attachment attachment : message.getAttachments()) {
            if (attachment.isImage()) {
                builder.setImage(attachment.getUrl());
                break;
            }
        }
        builder.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getAvatarUrl());

        return builder.build();
    }
}
