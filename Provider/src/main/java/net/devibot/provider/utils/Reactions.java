package net.devibot.provider.utils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Reactions {

    public static void reactionGUI(JDA jda, String issuerId, ReactionAwaiterUserType type, Message message, Consumer<String> callback, Consumer<String> timeOut, Collection<String> emotes) {
        emotes.forEach(emote -> message.addReaction(emote).queue());
        jda.addEventListener(new ReactionAwaiter(jda, issuerId, type, message.getId(), emotes, callback, timeOut));
    }

    public static void reactionGUI(JDA jda, String issuerId, ReactionAwaiterUserType type, Message message, Consumer<String> callback, Consumer<String> timeOut, Collection<String> emotes, long delay, TimeUnit unit) {
        emotes.forEach(emote -> message.addReaction(emote).queue());
        jda.addEventListener(new ReactionAwaiter(jda, issuerId, type, message.getId(), emotes, callback, timeOut, delay, unit));
    }

    public enum ReactionAwaiterUserType {
        ROLE, USER, PERMISSION
    }

    public static class ReactionAwaiter extends ListenerAdapter {

        private JDA jda;
        private String issuerId;
        private String messageId;
        private Consumer<String> callback;
        private Consumer<String> timeOut;
        private Collection<String> emotes;
        private ReactionAwaiterUserType type;

        private boolean wasUsed = false;

        private long delay;
        private TimeUnit unit;

        public ReactionAwaiter(JDA jda, String id, ReactionAwaiterUserType type, String message, Collection<String> emotes, Consumer<String> callback, Consumer<String> timeOut) {
                this(jda, id, type, message, emotes, callback, timeOut, 3, TimeUnit.MINUTES);
        }

        public ReactionAwaiter(JDA jda, String id, ReactionAwaiterUserType type, String message, Collection<String> emotes, Consumer<String> callback, Consumer<String> timeOut, long delay, TimeUnit unit) {
            this.jda = jda;
            this.issuerId = id;
            this.messageId = message;
            this.callback = callback;
            this.timeOut = timeOut;
            this.emotes = emotes;
            this.type = type;

            this.delay = delay;
            this.unit = unit;
        }

        @Override
        public void onMessageReactionAdd(MessageReactionAddEvent event) {
            if (!wasUsed) {
                event.getChannel().getMessageById(messageId).queueAfter(delay, unit, msg -> {
                    timeOut.accept(messageId);
                    event.getJDA().removeEventListener(this);
                }, failure -> event.getJDA().removeEventListener(this));
                wasUsed = true;
            }

            if (event.getUser() == event.getJDA().getSelfUser() || !event.getMessageId().equalsIgnoreCase(messageId)) return;

            if (event.getChannel().getType() != ChannelType.PRIVATE)
                event.getReaction().removeReaction(event.getUser()).queue(o -> {}, o -> {});

            switch (type) {
                case USER:
                    if (event.getUser().getId().equals(issuerId) && emotes.contains(event.getReactionEmote().getName()))
                        callback.accept(event.getReactionEmote().getName());
                case ROLE:
                    if (event.getGuild() != null && event.getGuild().getMember(event.getUser()).getRoles().stream().anyMatch(role -> role.getId().equals(issuerId)))
                        callback.accept(event.getUser().getId());
                case PERMISSION:
                    break;
            }
        }
    }

}
