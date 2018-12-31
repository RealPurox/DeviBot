package net.devibot.provider.listener;

import net.devibot.provider.core.DiscordBot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadyListener extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(ReadyListener.class);

    private DiscordBot discordBot;

    public ReadyListener(DiscordBot discordBot) {
        this.discordBot = discordBot;
    }

    @Override
    public void onReady(ReadyEvent event) {
        JDA.ShardInfo shardInfo = event.getJDA().getShardInfo();
        logger.info(shardInfo + " is ready.");
        //all shards have booted, we may now start all of our agents
        if(shardInfo.getShardId() == shardInfo.getShardTotal() - 1) {
            discordBot.getAgentManager().startAllAgents();
        }
    }
}
