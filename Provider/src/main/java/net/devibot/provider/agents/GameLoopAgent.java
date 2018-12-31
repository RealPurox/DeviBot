package net.devibot.provider.agents;

import net.devibot.provider.core.DiscordBot;
import net.devibot.provider.entities.Stats;
import net.dv8tion.jda.core.entities.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameLoopAgent extends Agent {

    private ScheduledExecutorService threadPool;
    private DiscordBot discordBot;

    private ScheduledFuture<?> gameLoopAgent;

    private boolean running = false;

    public GameLoopAgent(DiscordBot discordBot) {
        this.discordBot = discordBot;
        this.threadPool = discordBot.getThreadPool();
    }

    private class GameUpdateAgent implements Runnable {
        int index = 0;
        @Override
        public void run() {
            Stats stats = new Stats();
            List<Game> games = new ArrayList<>();

            games.add(Game.listening(stats.getUsers() + " users"));
            games.add(Game.playing("type !help"));
            games.add(Game.watching(stats.getGuilds() + " guilds"));
            games.add(Game.watching("www.devibot.net"));
            games.add(Game.playing("type !invite"));

            discordBot.getShardManager().setGame(games.get(index));

            index++;
            if (index > games.size() - 1) index = 0;
        }
    }

    @Override
    public void start() {
        super.start();
        this.gameLoopAgent = threadPool.scheduleAtFixedRate(new GameUpdateAgent(), 0, 1, TimeUnit.MINUTES);
        running = true;
    }

    @Override
    public void stop() {
        super.stop();
        if (this.gameLoopAgent != null)
            this.gameLoopAgent.cancel(true);
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
