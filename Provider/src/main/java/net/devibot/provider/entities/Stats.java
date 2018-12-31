package net.devibot.provider.entities;

import net.devibot.provider.Provider;
import net.devibot.provider.core.DiscordBot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

public class Stats {

    private int shards;
    private long guilds;
    private long users;
    private long channels;
    private int ping;


    public Stats() {
        DiscordBot discordBot = Provider.getInstance().getDiscordBot();

        this.shards = discordBot.getShardManager().getShardsTotal();
        this.guilds = 0;
        this.users = 0;
        this.channels = 0;
        this.ping = 0;

        for (JDA jda : discordBot.getShardManager().getShards()) {
            for (Guild guild : jda.getGuilds()) {
                this.guilds++;
                for (Member ignored : guild.getMembers()) this.users++;
                for (TextChannel ignored : guild.getTextChannels()) this.channels++;
                for (VoiceChannel ignored : guild.getVoiceChannels()) this.channels++;
            }
            for (PrivateChannel ignored : jda.getPrivateChannels()) this.channels++;
            this.ping += jda.getPing();
        }
        this.ping = this.ping / this.shards;
    }

    public int getShards() {
        return shards;
    }

    public long getGuilds() {
        return guilds;
    }

    public long getUsers() {
        return users;
    }

    public long getChannels() {
        return channels;
    }

    public int getPing() {
        return ping;
    }
}