package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.core.guild.DeviGuild;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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

            for (Guild guild : event.getJDA().getGuilds()) {
                // load all guild settings real quick so we can make sure they all have data stored in the database
                new DeviGuild(guild.getId(), devi);
                // re-open audio connection if the bot was shut down but is still in a voice channel once it's booted again.
                if (guild.getSelfMember().getVoiceState().inVoiceChannel()) {
                    guild.getAudioManager().openAudioConnection(guild.getSelfMember().getVoiceState().getChannel());
                }
            }
        }

        Guild staffGuild = jda.getGuildById("392264119102996480");
        if (staffGuild != null) {
            if (devi.getSettings().isDevBot()) System.out.println(staffGuild.getEmotes());
            Role adminRole = staffGuild.getRolesByName("Administrator", false).get(0);
            staffGuild.getMembersWithRoles(adminRole).forEach(member -> devi.getAdmins().add(member.getUser().getId()));
        }
    }
}
