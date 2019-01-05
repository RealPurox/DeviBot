package net.devibot.provider.listener;

import net.devibot.provider.core.DiscordBot;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class GuildJoinLeaveListener extends ListenerAdapter {

    private DiscordBot discordBot;

    public GuildJoinLeaveListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    

}
