package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.utils.JavaUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.jodah.expiringmap.ExpiringMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LearningListener extends ListenerAdapter {

    private Devi devi;
    //message id, message content
    //private ExpiringMap<Long, String> messages = ExpiringMap.builder().variableExpiration().build();
    //private ExpiringMap<Long, String> deletedMessages = ExpiringMap.builder().variableExpiration().build();

    public LearningListener(Devi devi) {
        this.devi = devi;
    }

    /*@Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        messages.put(event.getMessageIdLong(), event.getMessage().getContentRaw(), 30, TimeUnit.MINUTES);
    }

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        String message = messages.get(event.getMessageIdLong());
        if (message == null) return;

        List<String> similar = new ArrayList<>();

        //find similar deleted messages (over 0.8 similarity)
        for (String msg : deletedMessages.values()) {
            double similarity = JavaUtils.similarity(message, msg);
            if (similarity >= 0.8)
                similar.add(msg);
        }

        //add to deleted messages
        deletedMessages.put(event.getMessageIdLong(), message, 30, TimeUnit.MINUTES);

        System.out.println("Message deleted: " + message);
        System.out.println("Similar previously deleted messages: " + similar);
    }*/
}
