package net.devibot.provider.manager;

import net.devibot.core.agents.Agent;
import net.devibot.provider.agents.GameLoopAgent;
import net.devibot.provider.agents.KeepAliveAgent;
import net.devibot.provider.agents.MainframeInitializerAgent;
import net.devibot.provider.agents.StatsPusherAgent;
import net.devibot.provider.core.DiscordBot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AgentManager {

    public enum Type {
        STATS_PUSHER (true),
        GAME_LOOP (true),
        KEEP_ALIVE (true),
        MAINFRAME_INITIALIZER (false);

        private boolean startup;

        Type(boolean startup) {
            this.startup = startup;
        }

        public boolean isStartup() {
            return startup;
        }
    }

    private DiscordBot discordBot;
    private List<Agent> agents = new ArrayList<>();
    private LinkedHashMap<Type, Agent> agentTypeMap = new LinkedHashMap<>();

    public AgentManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
        registerAgent(Type.STATS_PUSHER, new StatsPusherAgent(discordBot));
        registerAgent(Type.GAME_LOOP, new GameLoopAgent(discordBot));
        registerAgent(Type.KEEP_ALIVE, new KeepAliveAgent(discordBot));
        registerAgent(Type.MAINFRAME_INITIALIZER, new MainframeInitializerAgent(discordBot));
    }

    private void registerAgent(Type type, Agent agent) {
        agents.add(agent);
        agentTypeMap.put(type, agent);
    }

    public void startAllAgents() {
        agentTypeMap.forEach((type, agent) -> {
            if (!agent.isRunning() && type.isStartup())
                agent.start();
        });

    }

    public void stopAllAgents() {
        agents.forEach(agent -> {
            if (!agent.isRunning())
                agent.stop();
        });
    }

    public List<Agent> getAgents() {
        return this.agents;
    }

    public Agent getAgent(Type type) {
        return agentTypeMap.get(type);
    }
}
