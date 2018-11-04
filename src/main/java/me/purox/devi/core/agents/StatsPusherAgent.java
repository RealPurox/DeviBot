package me.purox.devi.core.agents;

import me.purox.devi.core.Devi;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StatsPusherAgent implements Agent {

    private ScheduledExecutorService threadPool;
    private Devi devi;

    private ScheduledFuture<?> websiteStatsPusherAgent;
    private ScheduledFuture<?> discordBotListStatsPusherAgent;

    public StatsPusherAgent(ScheduledExecutorService threadPool, Devi devi) {
        this.threadPool = threadPool;
        this.devi = devi;
    }

    private class WebsiteStatsPusherAgent implements Runnable {
        //post stats every 2 min to the website
        @Override
        public void run() {
            Devi.Stats stats = devi.getCurrentStats();
            new RequestBuilder(devi.getOkHttpClient())
                    .setURL("https://www.devibot.net/api/stats")
                    .setRequestType(Request.RequestType.POST)
                    //body
                    .appendBody("shards", stats.getShards())
                    .appendBody("guilds", stats.getGuilds())
                    .appendBody("users", stats.getUsers())
                    .appendBody("channels", stats.getChannels())
                    .appendBody("average_ping", stats.getPing())
                    //header
                    .addHeader("Authorization", "Bearer " + devi.getSettings().getDeviAPIAuthorization())
                    .addHeader("Content-Type", "application/json")
                    .build().asStringSync();
        }
    }

    private class DiscordBotListStatsPusherAgent implements Runnable {
        //post every half an hour to bot lists
        @Override
        public void run() {
            Devi.Stats stats = devi.getCurrentStats();
            new RequestBuilder(devi.getOkHttpClient())
                    .setURL("https://discordbots.org/api/bots/354361427731152907/stats")
                    .setRequestType(Request.RequestType.POST)
                    //body
                    .appendBody("server_count", stats.getGuilds())
                    //header
                    .addHeader("Authorization", devi.getSettings().getDiscordBotsDotOrgToken())
                    .build().asStringSync();
        }
    }

    @Override
    public void start() {
        if (devi.getSettings().isDevBot()) return;

        this.websiteStatsPusherAgent = threadPool.scheduleAtFixedRate(new WebsiteStatsPusherAgent(), 0, 2, TimeUnit.MINUTES);
        this.discordBotListStatsPusherAgent = threadPool.scheduleAtFixedRate(new DiscordBotListStatsPusherAgent(), 0, 30, TimeUnit.MINUTES);
    }

    @Override
    public void stop() {
        if (devi.getSettings().isDevBot()) return;

        if (this.websiteStatsPusherAgent != null)
            this.websiteStatsPusherAgent.cancel(true);
        if (this.discordBotListStatsPusherAgent != null)
            this.discordBotListStatsPusherAgent.cancel(true);
    }
}
