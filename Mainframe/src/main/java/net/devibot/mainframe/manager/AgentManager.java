package net.devibot.mainframe.manager;

import net.devibot.core.agents.Agent;
import net.devibot.mainframe.Mainframe;
import net.devibot.mainframe.agents.KeepAliveAgent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AgentManager {

    private enum Type {
        KEEP_ALIVE
    }

    private Mainframe mainframe;
    private List<Agent> agents = new ArrayList<>();
    private LinkedHashMap<Type, Agent> agentTypeMap = new LinkedHashMap<>();

    public AgentManager(Mainframe mainframe) {
        this.mainframe = mainframe;
        registerAgent(Type.KEEP_ALIVE, new KeepAliveAgent(mainframe));
    }

    private void registerAgent(Type type, Agent agent) {
        agents.add(agent);
        agentTypeMap.put(type, agent);
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

    public Agent getAgent(Type type) {
        return agentTypeMap.get(type);
    }
}
