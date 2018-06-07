package me.purox.devi.core;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResponseWaiter extends ListenerAdapter {

    public class Response {

        private final User user;
        private final Message message;

        Response(User user, Message message) {
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

    ResponseWaiter() {
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

        if (timeout > 0 && timeUnit != null) {
            threadPool.schedule(() -> {
                if (waitingResponseSet.remove(waitingResponse) && timeOutAction != null) {
                    timeOutAction.run();
                }
            }, timeout, timeUnit);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        if (guild != null && waitingResponses.containsKey(guild.getId())) {
            Set<WaitingResponse> waitingResponses = this.waitingResponses.get(guild.getId());
            WaitingResponse[] toRemove = waitingResponses.toArray(new WaitingResponse[0]);
            waitingResponses.removeAll(Stream.of(toRemove).filter(i -> i.attempt(event, new Response(event.getAuthor(), event.getMessage()))).collect(Collectors.toSet()));
        }
    }

    private class WaitingResponse<T, R> {
        final Predicate<T> condition;
        final Consumer<R> action;

        WaitingResponse(Predicate<T> condition, Consumer<R> action)
        {
            this.condition = condition;
            this.action = action;
        }

        boolean attempt(T event, R response)
        {
            if(condition.test(event))
            {
                action.accept(response);
                return true;
            }
            return false;
        }
    }
}
