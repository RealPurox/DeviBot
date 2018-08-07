package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
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
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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
        JDA jda = event.getJDA();
        devi.getLogger().log(jda.getShardInfo() + " is ready");

        //last shard booted
        if(jda.getShardInfo().getShardId() == jda.getShardInfo().getShardTotal() - 1) {
            devi.startStatsPusher();
            devi.startVoteChecker();

            new Thread(() -> {
                for (Guild guild : event.getJDA().getGuilds()) {
                    // load all guild settings real quick so we can make sure they all have data stored in the database
                    new DeviGuild(guild.getId(), devi);
                    // re-open audio connection if the bot was shut down but is still in a voice channel once it's booted again.
                    if (guild.getSelfMember().getVoiceState().inVoiceChannel()) {
                        guild.getAudioManager().openAudioConnection(guild.getSelfMember().getVoiceState().getChannel());
                    }
                }
            }).start();
        }

        Guild staffGuild = jda.getGuildById("392264119102996480");
        if (staffGuild != null) {
            Role seniorAdministrators = staffGuild.getRolesByName("Senior Administrator", false).get(0);
            Role administrators = staffGuild.getRolesByName("Administrator", false).get(0);
            staffGuild.getMembersWithRoles(seniorAdministrators).forEach(member -> devi.getAdmins().add(member.getUser().getId()));
            staffGuild.getMembersWithRoles(administrators).forEach(member -> devi.getAdmins().add(member.getUser().getId()));
        }
    }


    @Override
    public void onException(ExceptionEvent event) {
        System.out.println("YES");
        devi.sendMessageToDevelopers(event.getCause().toString());
    }
}
