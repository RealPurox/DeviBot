package net.devibot.provider.manager;

import net.devibot.provider.agents.Agent;
import net.devibot.provider.agents.GameLoopAgent;
import net.devibot.provider.agents.StatsPusherAgent;
import net.devibot.provider.core.DiscordBot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AgentManager {

    private enum Type {
        STATS_PUSHER, GAME_LOOP
    }

    private DiscordBot discordBot;
    private List<Agent> agents = new ArrayList<>();
    private LinkedHashMap<Type, List<Agent>> agentTypeMap = new LinkedHashMap<>();

    public AgentManager(DiscordBot discordBot) {
        this.discordBot = discordBot;
        registerAgent(Type.STATS_PUSHER, new StatsPusherAgent(discordBot));
        registerAgent(Type.GAME_LOOP, new GameLoopAgent(discordBot));
    }

    private void registerAgent(Type type, Agent agent) {
        agents.add(agent);
        if (!agentTypeMap.containsKey(type))
            agentTypeMap.put(type, new ArrayList<>());
        agentTypeMap.get(type).add(agent);
    }

    public void startAllAgents() {
        agents.forEach(agent -> {
            if (!agent.isRunning())
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

    public List<Agent> getAgents(Type type) {
        return !agentTypeMap.containsKey(type) ? new ArrayList<>() : agentTypeMap.get(type);
    }
}
