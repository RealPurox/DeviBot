package net.devibot.provider.listener;

import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.entities.Emote;
import net.devibot.provider.utils.DiscordUtils;
import net.devibot.provider.utils.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class GuildJoinLeaveListener extends ListenerAdapter {

    private Logger logger = LoggerFactory.getLogger(GuildJoinLeaveListener.class);

    private DiscordBot discordBot;

    private String joinText = "Hey there, {0}! :wave: \n" +
            "Someone has added me to {1} and I thought I should let you know! I am Devi, a Discord bot with multiple different features. Let me show you some of the things I can do.\n" +
            "\n" +
            "- " + Emote.TWITCH + " Twitch Integration\n" +
            "- :map: Multilangauge Support\n" +
            "- " + Emote.BAN + " Auto Moderation Tools\n" +
            "- :shield: Moderation Logging\n" +
            "- :musical_note: Music Player\n" +
            "- :video_game: Custom Commands\n" +
            "\n" +
            "And that's not even all of it! You can explore more of my features by using !help in any message channel. To easily change your settings, use !settings.\n" +
            "\n" +
            "If you run into any problems or have any questions, feel free to join us and ask for help here <https://discord.gg/6Ac4uqZ>.\n" +
            "\n" +
            "Thank you for choosing Devi.";


    public GuildJoinLeaveListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        logger.info("Joined Guild " + event.getGuild().getName() + " (" + event.getGuild().getId() + ") owned by " + event.getGuild().getOwner().getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator() + " (" + event.getGuild().getOwnerId() + ")");
        List<User> serverManager = DiscordUtils.getGuildMembersWithPermissions(event.getGuild(), Permission.MANAGE_SERVER)
                .stream().map(Member::getUser).collect(Collectors.toList());

        for (User user : serverManager) {
            MessageUtils.sendPrivateMessage(user, joinText.replace("{0}", user.getAsMention()).replace("{1}", event.getGuild().getName()));
        }
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        logger.info("Left Guild " + event.getGuild().getName() + " (" + event.getGuild().getId() + ") owned by " + event.getGuild().getOwner().getUser().getName() + "#" + event.getGuild().getOwner().getUser().getDiscriminator() + " (" + event.getGuild().getOwnerId() + ")");
    }
}
