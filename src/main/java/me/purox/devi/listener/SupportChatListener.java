package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;

public class SupportChatListener extends ListenerAdapter {

    private Devi devi;

    private HashMap<String, String> userToChannel = new HashMap<>();
    private HashMap<String, String> channelToUser = new HashMap<>();
    private HashMap<String, String> channelStaffMember = new HashMap<>();

    public SupportChatListener(Devi devi) {
        this.devi = devi;
    }



    public HashMap<String, String> getUserToChannel() {
        return userToChannel;
    }

    public HashMap<String, String> getChannelToUser() {
        return channelToUser;
    }

    public HashMap<String, String> getChannelStaffMember() {
        return channelStaffMember;
    }
}
