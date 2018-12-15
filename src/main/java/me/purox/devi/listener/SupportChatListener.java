package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.entities.supportchat.SupportChat;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;

public class SupportChatListener extends ListenerAdapter {

    private Devi devi;

    public SupportChatListener(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        for (SupportChat supportChat : devi.getSupportChats()) {
            if (supportChat.getChannel().equals(event.getChannel().getId())) {

            }
        }
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        for (SupportChat supportChat : devi.getSupportChats()) {
            if (supportChat.getUser().equals(event.getAuthor().getId())) {
                
            }
        }
    }
}
