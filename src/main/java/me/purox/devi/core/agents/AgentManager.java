package me.purox.devi.core.agents;

import me.purox.devi.core.Devi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AgentManager {

    private enum AgentType {
        STATS_PUSHER, VOTE_CHECKER, GAME_LOOP
    }

    private Devi devi;
    private List<Agent> agents = new ArrayList<>();
    private LinkedHashMap<AgentType, List<Agent>> agentTypeAgentMap = new LinkedHashMap<>();

    public AgentManager(Devi devi) {
        this.devi = devi;
        registerAgent(AgentType.STATS_PUSHER, new StatsPusherAgent(devi));
        registerAgent(AgentType.VOTE_CHECKER, new VoteCheckAgent(devi));
        registerAgent(AgentType.GAME_LOOP, new GameLoopAgent(devi));
    }

    private void registerAgent(AgentType agentType, Agent agent) {
        agents.add(agent);
        if (!agentTypeAgentMap.containsKey(agentType))
            agentTypeAgentMap.put(agentType, new ArrayList<>());
        agentTypeAgentMap.get(agentType).add(agent);
    }

    public void startAllAgents() {
        agents.forEach(agent -> {
            if (!agent.isRunning())
                agent.start();
        });
    }

    public void stopAllAgent() {
        agents.forEach(agent -> {
            if (agent.isRunning())
                agent.stop();
        });
    }

    public List<Agent> getAgents(AgentType agent) {
        return !agentTypeAgentMap.containsKey(agent) ? new ArrayList<>() : agentTypeAgentMap.get(agent);
    }

}
