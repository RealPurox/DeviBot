package me.purox.devi.listener;

import me.purox.devi.core.Devi;
import me.purox.devi.core.Language;
import me.purox.devi.core.guild.DeviGuild;
import me.purox.devi.core.guild.GuildSettings;
import me.purox.devi.utils.MessageUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
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
        DeviGuild deviGuild = devi.getDeviGuild(event.getGuild().getId());
        Language language = Language.getLanguage(deviGuild.getSettings().getStringValue(GuildSettings.Settings.LANGUAGE));

        User owner = event.getGuild().getOwner().getUser();

        StringBuilder builder = new StringBuilder();

        builder.append("Howdy ").append(owner.getAsMention()).append(", thank you very much for adding me to ").append("**").append(event.getGuild().getName()).append("**").append(" :wave:").append("\n\n");
        builder.append("As you've probably seen already, I'm a Discord bot. ");
        builder.append("My main purpose is to help you and your moderator colleagues to moderate your server.\n\n");
        builder.append("`-` ").append("English is not your main language? Don't worry buddy, I gotcha! We currently support the following languages: ").append(Language.getAllLanguagesBeautiful()).append(".\n\n");
        builder.append("`-` ").append("My default prefix is ").append("`").append(devi.getSettings().getDefaultPrefix()).append("`").append(", but you can change it anytime with `!settings prefix <prefix>`\n\n");
        builder.append("`-` ").append("I would strongly recommend you to checkout my auto-mod and mod-log features using `!automod` and `!modlog`\n\n");
        builder.append("Okay, we've talked enough. If you want to get a list of all commands simple type `!help`. ");
        builder.append("OH and before I forget it, you should definitely checkout our super duper awesome website <https://www.devibot.net/> !!");

        MessageUtils.sendPrivateMessageAsync(event.getGuild().getOwner().getUser(), builder.toString());
        System.out.println("[INFO] Joined Guild " + event.getGuild().getName() + " ( " + event.getGuild().getId() + " ) " + event.getGuild().getOwner().getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        System.out.println("[INFO] Left Guild " + event.getGuild().getName() + " ( " + event.getGuild().getId() + " ) " + event.getGuild().getOwner().getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator());
    }

    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();
        System.out.println(jda.getShardInfo() + " is ready");

        //last shard booted
        if(jda.getShardInfo().getShardId() == jda.getShardInfo().getShardTotal() - 1) {
            devi.startStatsPusher();

            for (Guild guild : event.getJDA().getGuilds()) {
                // load all guild settings real quick so we can make sure they all have data stored in the database
                new DeviGuild(guild.getId(), devi);
                // re-open audio connection if the bot was shut down but is still in a voice channel once it's booted again.
                if (guild.getSelfMember().getVoiceState().inVoiceChannel()) {
                    guild.getAudioManager().openAudioConnection(guild.getSelfMember().getVoiceState().getChannel());
                    //create player if it doesn't exist
                    devi.getMusicManager().getPlayer(guild);
                }
            }
        }

        Guild staffGuild = jda.getGuildById("392264119102996480");
        if (staffGuild != null) {
            if (devi.getSettings().isDevBot()) System.out.println(staffGuild.getEmotes());
            Role adminRole = staffGuild.getRolesByName("Senior Administrator", false).get(0);
            staffGuild.getMembersWithRoles(adminRole).forEach(member -> devi.getAdmins().add(member.getUser().getId()));
        }
    }
}
