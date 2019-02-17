package net.devibot.provider.agents;

import net.devibot.core.Core;
import net.devibot.core.agents.Agent;
import net.devibot.core.utils.DiscordWebhook;
import net.devibot.provider.core.DiscordBot;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class KeepAliveAgent extends Agent {

    private ScheduledExecutorService threadPool;
    private DiscordBot discordBot;

    private ScheduledFuture<?> keepAliveAgent;

    private boolean running = false;

    private long lastKeepAliveTime = System.currentTimeMillis();
    private boolean webhookSent = false;

    public KeepAliveAgent(DiscordBot discordBot) {
        this.discordBot = discordBot;
        this.threadPool = discordBot.getScheduledThreadPool();
    }

    private class KeepAliveRunnable implements Runnable {

        @Override
        public void run() {
            if (System.currentTimeMillis() - lastKeepAliveTime >= 30000 && !webhookSent) { //30 seconds no keep alive
                discordBot.enableRestrictedMode();
                webhookSent = true;
                DiscordWebhook webhook = new DiscordWebhook(Core.CONFIG.getMonitoringRoomWebhook());
                webhook.setContent(":warning:__Something seems to be wrong__:warning:\n" + (Core.CONFIG.isDevMode() ? "<@222753093559910400>\n" : "@everyone\n") + "`" + discordBot.getProvider().toString() + "`:\n```Provider lost communication with mainframe.\n\nNow entering restriction mode and attempting to restore connection to mainframe!```");
                webhook.execute();
            }

            if (System.currentTimeMillis() - lastKeepAliveTime < 30000 && webhookSent) {
                webhookSent = false;
                DiscordWebhook webhook = new DiscordWebhook(Core.CONFIG.getMonitoringRoomWebhook());
                webhook.setContent(":white_check_mark:__Issues have been resolved__ :white_check_mark:\n\n`" + discordBot.getProvider().toString() + "`:\n```Provider restored communication with mainframe.```");
                webhook.execute();
            }
        }

    }

    public void updateLastKeepAliveTime() {
        this.lastKeepAliveTime = System.currentTimeMillis();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void start() {
        super.start();
        this.keepAliveAgent = threadPool.scheduleAtFixedRate(new KeepAliveRunnable(), 0, 15, TimeUnit.SECONDS);
        running = true;
    }

    @Override
    public void stop() {
        super.stop();
        if (this.keepAliveAgent != null)
            this.keepAliveAgent.cancel(true);
        running = false;
    }
}
