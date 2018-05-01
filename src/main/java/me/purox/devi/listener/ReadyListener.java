package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReadyListener extends ListenerAdapter {

    private Devi devi;

    public ReadyListener(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();
        System.out.println(jda.getShardInfo() + " is ready");

        //last shard booted
        if(jda.getShardInfo().getShardId() == jda.getShardInfo().getShardTotal() - 1) {
            if (!devi.getSettings().isDevBot()) {
                devi.startStatsPusher();
            }
        }

        Guild staffGuild = jda.getGuildById("392264119102996480");
        if (staffGuild != null) {
            System.out.println(staffGuild.getEmotes());
            Role adminRole = staffGuild.getRolesByName("Administrator", false).get(0);
            staffGuild.getMembersWithRoles(adminRole).forEach(member -> devi.getAdmins().add(member.getUser().getId()));
        }
    }
}
