package me.purox.devi.utils;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
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

    public static void reactionGUI(JDA jda, String id, boolean user, Message message, Consumer<String> callback, Consumer<String> timeout, Collection<String> emotes) {
        emotes.forEach(emote -> message.addReaction(emote).queue());
        jda.addEventListener(new ReactionWaiter(jda, id, user, message.getId(), emotes, callback, timeout));
    }

    public static void reactionGUI(JDA jda, String id, boolean user, Message message, Consumer<String> callback, Consumer<String> timeout, Collection<String> emotes, long delay, TimeUnit unit) {
        emotes.forEach(emote -> message.addReaction(emote).queue());
        jda.addEventListener(new ReactionWaiter(jda, id, user, message.getId(), emotes, callback, timeout, delay, unit));
    }

    public static class ReactionWaiter extends ListenerAdapter {

        private JDA jda;
        private String id;
        private String message;
        private Consumer<String> callback;
        private Consumer<String> timeout;
        private Collection<String> emotes;
        private boolean user;

        private boolean used = false;

        private long delay;
        private TimeUnit unit;

        public ReactionWaiter(JDA jda, String id, boolean user, String message, Collection<String> emotes, Consumer<String> callback, Consumer<String> timeout) {
            this(jda, id, user, message, emotes, callback, timeout, 2, TimeUnit.MINUTES);
        }

        public ReactionWaiter(JDA jda, String id, boolean user, String message, Collection<String> emotes, Consumer<String> callback, Consumer<String> timeout, long delay, TimeUnit unit) {
            this.jda = jda;
            this.id = id;
            this.message = message;
            this.callback = callback;
            this.timeout = timeout;
            this.emotes = emotes;
            this.user = user;

            this.delay = delay;
            this.unit = unit;
        }

        @Override
        public void onMessageReactionAdd(MessageReactionAddEvent event) {
            if (!used) {
                event.getChannel().getMessageById(message).queueAfter(delay, unit,
                        msg -> {
                            timeout.accept(message);
                            event.getJDA().removeEventListener(this);
                        }, throwable -> event.getJDA().removeEventListener(this));
                used = true;
            }

            if (event.getUser() == event.getJDA().getSelfUser() || !event.getMessageId().equals(message)) return;

            if (event.getChannel().getType() != ChannelType.PRIVATE)
                event.getReaction().removeReaction(event.getUser()).queue(o -> {}, o -> {});

            //we need a user
            if (user) {
                if (event.getUser().getId().equals(id) && emotes.contains(event.getReactionEmote().getName())) {
                    callback.accept(event.getReactionEmote().getName());
                }
            }
            //we need a role
            else {
                if (event.getGuild() != null && event.getGuild().getMember(event.getUser()).getRoles().stream().anyMatch(role -> role.getId().equals(id))) {
                    callback.accept(event.getUser().getId());
                }
            }
        }
    }

}
