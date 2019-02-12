package net.devibot.mainframe.agents;

import net.devibot.core.agents.Agent;
import net.devibot.mainframe.Mainframe;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class KeepAliveAgent extends Agent {

    private ScheduledExecutorService threadPool;
    private Mainframe mainframe;

    private ScheduledFuture<?> keepAliveAgent;

    private boolean running;

    public KeepAliveAgent(Mainframe mainframe) {
        this.mainframe = mainframe;
        this.threadPool = mainframe.getScheduledThreadPool();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void start() {
        super.start();
        this.keepAliveAgent = threadPool.scheduleAtFixedRate(() -> mainframe.getProviderManager().sendKeepAliveMessage(), 15, 15, TimeUnit.SECONDS);
        this.running = true;
    }

    @Override
    public void stop() {
        super.stop();
        if (this.keepAliveAgent != null)
            this.keepAliveAgent.cancel(true);
        this.running = false;
    }
}
