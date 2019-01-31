package net.devibot.mainframe.agents;

import net.devibot.core.Core;
import net.devibot.core.agents.Agent;
import net.devibot.core.database.DatabaseManager;
import net.devibot.core.entities.DeviGuild;
import net.devibot.mainframe.Mainframe;
import org.bson.Document;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SettingsUpdateAgent extends Agent {

    private ScheduledExecutorService threadPool;
    private Mainframe mainframe;

    private ScheduledFuture<?> settingsUpdateAgent;

    private boolean running = false;

    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private LinkedList<DeviGuild> queue = new LinkedList<>();

    public SettingsUpdateAgent(Mainframe mainframe) {
        this.mainframe = mainframe;
        this.threadPool = mainframe.getScheduledThreadPool();
    }

    private class GuildSettingsUpdateAgent implements Runnable {
        @Override
        public void run() {
            if (queue.isEmpty()) return;
            DeviGuild deviGuild = queue.remove();
            Document document = new Document(new JSONObject(Core.GSON.toJson(deviGuild)).toMap());
            databaseManager.saveToDatabase("guilds", document, deviGuild.getId());
        }
    }

    public void appendQueue(DeviGuild deviGuild) {
        this.queue.add(deviGuild);
    }

    @Override
    public void start() {
        super.start();
        this.settingsUpdateAgent = threadPool.scheduleAtFixedRate(new GuildSettingsUpdateAgent(), 0, 5, TimeUnit.SECONDS);
        running = true;
    }

    @Override
    public void stop() {
        super.stop();
        if (this.settingsUpdateAgent != null)
            this.settingsUpdateAgent.cancel(true);
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

}
