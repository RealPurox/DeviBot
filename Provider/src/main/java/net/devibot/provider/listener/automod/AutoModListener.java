package net.devibot.provider.listener.automod;

import net.devibot.core.entities.DeviGuild;
import net.devibot.core.entities.automod.AutoModAntiInvites;
import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.listener.automod.predicates.InvitesPredicate;
import net.devibot.provider.utils.MessageUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.function.Predicate;

public class AutoModListener extends ListenerAdapter {

    private DiscordBot discordBot;

    public AutoModListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        DeviGuild deviGuild = discordBot.getCacheManager().getDeviGuildCache().getDeviGuild(event.getGuild().getId());
        if (!deviGuild.getAutoMod().isEnabled()) return;

        //<editor-fold desc="invites">
        AutoModAntiInvites antiInvites = deviGuild.getAutoMod().getAntiInvites();

        if (antiInvites.isEnabled()) {
            Predicate<Message> predicate = new InvitesPredicate();
            if (predicate.test(event.getMessage())) {
                Runnable runnable = () -> MessageUtils.sendMessage(event.getChannel(), event.getAuthor().getAsMention() + ", don't post invite links again!");
                event.getMessage().delete().queue(success -> runnable.run(), failure -> runnable.run());
            }
        }
        //</editor-fold>

    }
}
