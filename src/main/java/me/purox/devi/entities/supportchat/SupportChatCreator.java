package me.purox.devi.entities.supportchat;

import me.purox.devi.core.Devi;
import me.purox.devi.entities.Emote;
import me.purox.devi.utils.MessageUtils;
import me.purox.devi.utils.Reactions;
import net.dv8tion.jda.core.entities.*;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SupportChatCreator {

    private String userMessage = "Thanks a lot for using our support chat feature. This channel will temporarily be transformed into a support chat and you will no longer be able to use commands in here.\n\n" +
            "Our staff members will be able to see every message you send in this channel and so will you be able to see all messages they send in the support chat, therefore you should not " +
            "provide any sensitive information like emails, passwords, etc.\n\n";

    private String staffMessage = "Hello <@&422327399401652224>, {user} is in need of assistance. Please claim the support chat by reacting with ☑.";

    private Devi devi;

    private boolean hasUserReacted = false;

    private String userId;
    private String staffId;
    private String channelId;

    private Consumer<SupportChat> consumer;

    public SupportChatCreator(String userId, Devi devi) {
        this.userId = userId;
        this.devi = devi;
    }

    public void start(Consumer<SupportChat> consumer) {
        this.consumer = consumer;
        User user = devi.getShardManager().getUserById(userId);
        if (user == null) return;

        MessageUtils.sendPrivateMessageAsync(user, userMessage + "Please react with ☑ to acknowledge that you've read the above.", message -> handleUserReaction(user, message));
    }

    private void handleUserReaction(User user, Message message) {
        Reactions.reactionGUI(user.getJDA(), userId, true, message, callback -> {
            hasUserReacted = true;

            Guild staffGuild = devi.getStaffGuild();
            if (staffGuild == null) return;


            Category category = staffGuild.getCategoriesByName("support chats", true).get(0);
            if (category == null) return;


            message.editMessage(userMessage + Emote.SUCCESS + " | Awesome! All staff members have been alerted and one of them will get back to you as soon as possible. If you do not get a response withing the next 15 minutes please try again at a later point of time as there might not be a staff member available at the moment").queue(placeHolder -> {}, placeHolder -> {});
            createSupportChannel(category, user, message);
        }, timeOut -> {
            if (!hasUserReacted)
                message.editMessage(userMessage + Emote.ERROR + " | Sorry, you took to long to react to my message. If you are still in need of support, run the support command again.").queue(placeHolder -> {}, placeHolder -> {});
        }, Collections.singletonList("☑"), 3, TimeUnit.MINUTES);
    }

    private void createSupportChannel(Category category, User user, Message message) {
        category.createTextChannel(user.getName() + "_" + user.getDiscriminator()).queue(channel -> {
            channelId = channel.getId();
            MessageUtils.sendMessageAsync((MessageChannel) channel, staffMessage.replace("{user}", user.getName() + "#" + user.getDiscriminator() + " (" + userId + ")"), sent -> handleStaffReaction(channel, sent, user));
        }, failure -> {
            message.editMessage(userMessage + Emote.ERROR + " | Something went wrong while attempting to setup your support chat. Our developers have been alerted and will work on it as soon as they see it.").queue(placeHolder -> {}, placeHolder -> {});
            devi.sendMessageToDevelopers("Failed to create support channel for user " + user + "\n\nThrowable:" + failure);
        });
    }

    private void handleStaffReaction(Channel channel, Message staffMessage, User user) {
        Reactions.reactionGUI(staffMessage.getJDA(), /*supporter role id*/"422327399401652224", false, staffMessage, staff -> {
            staffId = staff;
            if (consumer != null) consumer.accept(new SupportChat(userId, staffId, channelId));
            consumer = null;
        }, timeOut -> {
            MessageUtils.sendPrivateMessageAsync(user, Emote.INFO + " | It seems like none of our staff members are available at the moment. Please try again at a later point of time.");
            channel.delete().queue(placeHolder -> {}, placeHolder -> {});
        }, Collections.singletonList("☑"), 15, TimeUnit.MINUTES);
    }


}
