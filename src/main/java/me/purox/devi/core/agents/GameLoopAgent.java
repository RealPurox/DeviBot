package me.purox.devi.core.agents;

import me.purox.devi.core.Devi;
import net.dv8tion.jda.core.entities.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GameLoopAgent extends Agent {


    private ScheduledExecutorService threadPool;
    private Devi devi;

    private ScheduledFuture<?> gameLoopAgent;

    private boolean running;

    public GameLoopAgent(Devi devi) {
        super(devi);
        this.threadPool = devi.getThreadPool();
        this.devi = devi;
    }

    private class VoteCheckerAgent implements Runnable {
        // update voters every 5 mins
        int index = 0;
        @Override
        public void run() {
            List<Game> games = new ArrayList<>();

            Devi.Stats stats = devi.getCurrentStats();

            games.add(Game.listening(stats.getUsers() + " users"));
            games.add(Game.playing("type !help"));
            games.add(Game.watching(stats.getGuilds() + " guilds"));
            games.add(Game.watching("www.devibot.net"));
            games.add(Game.playing("type !invite"));

            devi.getShardManager().setGame(games.get(index));

            index++;
            if (index > games.size() - 1) index = 0;
        }
    }

    @Override
    void start() {
        super.start();
        this.gameLoopAgent = threadPool.scheduleAtFixedRate(new VoteCheckerAgent(), 0, 1, TimeUnit.MINUTES);
        running = true;
    }

    @Override
    boolean isRunning() {
        return running;
    }

    @Override
    void stop() {
        super.stop();
        if (this.gameLoopAgent != null)
            this.gameLoopAgent.cancel(true);
        running = false;
    }
}
