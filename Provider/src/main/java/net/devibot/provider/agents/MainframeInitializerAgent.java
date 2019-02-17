package net.devibot.provider.agents;

import net.devibot.core.agents.Agent;
import net.devibot.provider.core.DiscordBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainframeInitializerAgent extends Agent {

    private static Logger logger = LoggerFactory.getLogger(MainframeInitializerAgent.class);

    private ScheduledExecutorService threadPool;
    private DiscordBot discordBot;

    private ScheduledFuture<?> initializerAgent;

    private boolean running = false;


    public MainframeInitializerAgent(DiscordBot discordBot) {
        this.discordBot = discordBot;
        this.threadPool = discordBot.getScheduledThreadPool();
    }

    private class InitializerRunnable implements Runnable {

        @Override
        public void run() {
            discordBot.getMainframeManager().initialRequest(false, success -> {
                if (success) {
                    logger.info("(X) Mainframe was initialized successfully!");
                    discordBot.disableRestrictedMode();
                } else {
                    logger.info("(X) Mainframe initialization failed! Next attempt scheduled in 15 seconds.");
                }
            });
        }

    }

    @Override
    public boolean isRunning() {
        return running;
    }


    @Override
    public void start() {
        super.start();
        this.initializerAgent = threadPool.scheduleAtFixedRate(new InitializerRunnable(), 0, 15, TimeUnit.SECONDS);
        running = true;
    }

    @Override
    public void stop() {
        super.stop();
        if (this.initializerAgent != null)
            this.initializerAgent.cancel(true);
        running = false;
    }

}
