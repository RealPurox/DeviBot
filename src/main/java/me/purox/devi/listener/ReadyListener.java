package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.core.agents.StatsPusherAgent;
import me.purox.devi.core.agents.VoteCheckAgent;
import me.purox.devi.entities.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ExceptionEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.concurrent.ScheduledExecutorService;

public class ReadyListener extends ListenerAdapter {

    private Devi devi;

    public ReadyListener(Devi devi) {
        this.devi = devi;
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        User owner = event.getGuild().getOwner().getUser();

        String builder = "Howdy " + owner.getAsMention() + ", thank you very much for adding me to " + "**" + event.getGuild().getName() + "**" + " :wave:" + "\n\n" +
                "As you've probably seen already, I'm a Discord bot. " +
                "My main purpose is to help you and your moderator colleagues to moderate your server. I'm also a utility bot with a huge variation of features and commands!\n\n" +
                "`-` " + "English is not your main language? Don't worry buddy, I gotcha! We currently support the following languages: " + Language.getAllLanguagesBeautiful() + ".\n\n" +
                "`-` " + "My default prefix is " + "`" + devi.getSettings().getDefaultPrefix() + "`" + ", but you can change it anytime with `!settings prefix <prefix>`\n\n" +
                "`-` " + "I would strongly recommend you to checkout my auto-mod and mod-log features using `!automod` and `!modlog`\n\n" +
                "Okay, we've talked enough. If you want to get a list of all commands simply type `!help`. " +
                "And before I forget to say it, you should definitely check out our awesome website <https://www.devibot.net/>";
        MessageUtils.sendPrivateMessageAsync(event.getGuild().getOwner().getUser(), builder);
        devi.getLogger().log("Joined Guild " + event.getGuild().getName() + " ( " + event.getGuild().getId() + " ) " + event.getGuild().getOwner().getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        devi.getLogger().log("Left Guild " + event.getGuild().getName() + " ( " + event.getGuild().getId() + " ) " + event.getGuild().getOwner().getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator());
    }

    @Override
    public void onReady(ReadyEvent event) {
        JDA.ShardInfo shardInfo = event.getJDA().getShardInfo();
        if(shardInfo.getShardId() == shardInfo.getShardTotal() - 1) {
            devi.getLogger().log(shardInfo + " is ready");
            devi.getAgentManager().startAllAgents();
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        Guild guild = event.getGuild();

        //staff guild is ready => load admins
        if (guild.getId().equals("392264119102996480")) {
            Role seniorAdministrators = guild.getRolesByName("Senior Administrator", false).get(0);
            Role administrators = guild.getRolesByName("Administrator", false).get(0);
            guild.getMembersWithRoles(seniorAdministrators).forEach(member -> devi.getAdmins().add(member.getUser().getId()));
            guild.getMembersWithRoles(administrators).forEach(member -> devi.getAdmins().add(member.getUser().getId()));
        }

        // re-open audio connection if the bot was shut down but is still in a voice channel once it's booted again.
        if (guild.getSelfMember().getVoiceState().inVoiceChannel()) {
            devi.getThreadPool().submit(() -> guild.getAudioManager().openAudioConnection(guild.getSelfMember().getVoiceState().getChannel()));
        }
    }

    @Override
    public void onException(ExceptionEvent event) {
        devi.sendMessageToDevelopers(event.getCause().toString());
    }
}
