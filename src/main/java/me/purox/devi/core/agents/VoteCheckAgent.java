package me.purox.devi.core.agents;

import me.purox.devi.core.Devi;
import me.purox.devi.request.Request;
import me.purox.devi.request.RequestBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class VoteCheckAgent extends Agent {

    private ScheduledExecutorService threadPool;
    private Devi devi;

    private ScheduledFuture<?> voteCheckerAgent;

    private boolean running = false;

    public VoteCheckAgent(Devi devi) {
        super(devi);
        this.threadPool = devi.getThreadPool();
        this.devi = devi;
    }

    private class VoteCheckerAgent implements Runnable {
        // update voters every 5 mins
        @Override
        public void run() {
            new RequestBuilder(devi.getOkHttpClient()).setRequestType(Request.RequestType.GET)
                    .setURL("https://discordbots.org/api/bots/354361427731152907/votes")
                    .addHeader("Authorization", devi.getSettings().getDiscordBotsDotOrgToken())
                    .build().asString(json -> {
                if (json.getBody().startsWith("{")) return;
                JSONArray jsonArray = new JSONArray(json.getBody());
                jsonArray.forEach(object -> {
                    JSONObject vote = (JSONObject) object;
                    if (!devi.getVoters().contains(vote.getString("id")))
                        devi.getVoters().add(vote.getString("id"));
                });
            });
        }
    }

    @Override
    public void start() {
        super.start();
        this.voteCheckerAgent = threadPool.scheduleAtFixedRate(new VoteCheckerAgent(), 0, 5, TimeUnit.MINUTES);
        this.running = true;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void stop() {
        super.stop();
        if (this.voteCheckerAgent != null)
            this.voteCheckerAgent.cancel(true);
        this.running = false;
    }
}
