package me.purox.devi.core.waiter;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class WaitingResponseBuilder {

    private WaiterType waiterType;
    private User executor;
    private MessageChannel channel;

    public WaitingResponseBuilder(User executor, MessageChannel channel) {
        this.executor = executor;
        this.channel = channel;

        //default 
    }

    private enum WaiterType {
        SELECTOR, CHANNEL, ROLE, USER
    }
}