package net.devibot.provider.agents;

import net.devibot.core.agents.Agent;
import net.devibot.core.request.Request;
import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.entities.Stats;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StatsPusherAgent extends Agent {

    private ScheduledExecutorService threadPool;
    private DiscordBot discordBot;

    private ScheduledFuture<?> websiteStatsPusherAgent;
    private ScheduledFuture<?> botListsStatsPusherAgent;

    private boolean running = false;

    public StatsPusherAgent(DiscordBot discordBot) {
        this.discordBot = discordBot;
        this.threadPool = discordBot.getScheduledThreadPool();
    }

    private class WebsiteStatsPusherAgent implements Runnable {
        @Override
        public void run() {
            Stats stats = new Stats();
            discordBot.newRequestBuilder()
                    .setURL("https://discordbots.org/api/bots/354361427731152907/stats")
                    .setRequestType(Request.Type.POST)
                    //body
                    .appendBody("server_count", stats.getGuilds())
                    //header
                    .addHeader("Authorization", discordBot.getConfig().getWebsiteAuthenticationKey())
                    .build().executeSync(); //sync bc we already have it in a different thread
        }
    }

    private class BotListsStatsPusherAgent implements Runnable {
        //post every half an hour to bot lists
        @Override
        public void run() {
            Stats stats = new Stats();
            discordBot.newRequestBuilder()
                    .setURL("https://discordbots.org/api/bots/354361427731152907/stats")
                    .setRequestType(Request.Type.POST)
                    //body
                    .appendBody("server_count", stats.getGuilds())
                    //header
                    .addHeader("Authorization", discordBot.getConfig().getDiscordBotsDotOrgToken())
                    .build().executeSync(); //sync bc we already have it in a different thread
            //todo add devi to more lists and implement stats pushing
        }
    }

    @Override
    public void start() {
        if (discordBot.getConfig().isDevMode()) return; //Don't push stats when running in dev mode
        super.start();

        this.websiteStatsPusherAgent = threadPool.scheduleAtFixedRate(new WebsiteStatsPusherAgent(), 0 , 2, TimeUnit.MINUTES);
        this.botListsStatsPusherAgent = threadPool.scheduleAtFixedRate(new BotListsStatsPusherAgent(), 0, 30, TimeUnit.MINUTES);
        this.running = true;
    }

    @Override
    public void stop() {
        if (discordBot.getConfig().isDevMode()) return;
        super.stop();

        if (this.websiteStatsPusherAgent != null)
            this.websiteStatsPusherAgent.cancel(true);
        if (this.botListsStatsPusherAgent != null)
            this.botListsStatsPusherAgent.cancel(true);
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
