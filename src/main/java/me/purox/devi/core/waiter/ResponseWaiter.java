package me.purox.devi.core.waiter;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ResponseWaiter {

    public static class Response {

        private final User user;
        private final Message message;

        public Response(User user, Message message) {
            this.user = user;
            this.message = message;
        }

        public Message getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }
    }

    //thread executor
    private ScheduledExecutorService threadPool;
    //Guild ID, User ID
    private HashMap<String, Set<WaitingResponse>> waitingResponses;

    public ResponseWaiter() {
        this.threadPool = Executors.newSingleThreadScheduledExecutor();
        this.waitingResponses = new HashMap<>();
    }

    public ScheduledExecutorService getThreadPool() {
        return threadPool;
    }

    public void waitForResponse(Guild guild, Predicate<MessageReceivedEvent> condition, Consumer<Response> action, long timeout, TimeUnit timeUnit, Runnable timeOutAction) {
        WaitingResponse waitingResponse = new WaitingResponse<>(condition, action);

        Set<WaitingResponse> waitingResponseSet;

        if (!this.waitingResponses.containsKey(guild.getId()))
            this.waitingResponses.put(guild.getId(), new HashSet<>());

        waitingResponseSet = this.waitingResponses.get(guild.getId());
        waitingResponseSet.add(waitingResponse);

        if (timeout > 0) {
            threadPool.schedule(() -> {
                if (waitingResponseSet.remove(waitingResponse) && timeOutAction != null) {
                    timeOutAction.run();
                }
            }, timeout, timeUnit);
        }
    }

    public class WaitingResponse<T, R> {
        final Predicate<T> condition;
        final Consumer<R> action;

        WaitingResponse(Predicate<T> condition, Consumer<R> action) {
            this.condition = condition;
            this.action = action;
        }

        public boolean attempt(T event, R response) {
            if(condition.test(event)) {
                action.accept(response);
                return true;
            }
            return false;
        }
    }

    public HashMap<String, Set<WaitingResponse>> getWaitingResponses() {
        return waitingResponses;
    }

    public boolean checkUser(MessageReceivedEvent event, String messageID, String authorID, String channelID) {
        return event.getAuthor().getId().equals(authorID) &&
                event.getChannel().getId().equalsIgnoreCase(channelID) &&
                !event.getMessageId().equals(messageID) &&
                !event.getAuthor().isBot();
    }
}
