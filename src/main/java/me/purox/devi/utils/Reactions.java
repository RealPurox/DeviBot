package me.purox.devi.utils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Reactions {

    private static final ScheduledExecutorService THREAD_POOL = Executors.newSingleThreadScheduledExecutor();

    public static void reactionGUI(User user, Message message, Consumer<String> callback, Consumer<String> timeout, Collection<String> emotes) {
        emotes.forEach(emote -> message.addReaction(emote).queue());
        user.getJDA().addEventListener(new ReactionWaiter(user.getJDA(), user.getId(), message.getId(), callback, timeout));
    }

    public static class ReactionWaiter extends ListenerAdapter {

        private JDA jda;
        private String user;
        private String message;
        private Consumer<String> callback;
        private Consumer<String> timeout;

        private boolean used = false;

        public ReactionWaiter(JDA jda, String user, String message, Consumer<String> callback, Consumer<String> timeout) {
            this.jda = jda;
            this.user = user;
            this.message = message;
            this.callback = callback;
            this.timeout = timeout;
        }

        @Override
        public void onMessageReactionAdd(MessageReactionAddEvent event) {
            if (!used) {
                event.getChannel().getMessageById(message).queueAfter(1, TimeUnit.MINUTES,
                        msg -> {
                            timeout.accept(message);
                            event.getJDA().removeEventListener(this);
                        }, throwable -> event.getJDA().removeEventListener(this));
                used = true;
            }

            if (event.getUser() == event.getJDA().getSelfUser() || !event.getMessageId().equals(message)) return;

            event.getReaction().removeReaction(event.getUser()).queue(o -> {}, o -> {});
            if (event.getUser().getId().equals(user)) {
                callback.accept(event.getReactionEmote().getName());
            }
        }
    }

}
