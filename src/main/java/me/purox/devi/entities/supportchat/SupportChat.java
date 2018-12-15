package me.purox.devi.entities.supportchat;

import me.purox.devi.core.Devi;
import org.json.JSONObject;

public class SupportChat {

    /*
    REDIS:
        support_chats:
            user (id):
                {staff: id, channel: id}
    */

    private String user;
    private String staff;
    private String channel;

    public SupportChat(String user, String staff, String channel) {
        this.user = user;
        this.staff = staff;
        this.channel = channel;
    }

    public void save(Devi devi) {
        devi.getRedisManager().getSender().hset("support_chats", user, new JSONObject().put("staff", staff).put("channel", channel).toString());
        devi.getSupportChats().add(this);
    }

    public void remove(Devi devi) {
        devi.getRedisManager().getSender().hget("support_chats", user);
    }

    public String getUser() {
        return user;
    }

    public String getStaff() {
        return staff;
    }

    public String getChannel() {
        return channel;
    }
}
