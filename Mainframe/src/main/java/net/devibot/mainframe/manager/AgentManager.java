package net.devibot.mainframe.manager;

import net.devibot.core.agents.Agent;
import net.devibot.mainframe.Mainframe;
import net.devibot.mainframe.agents.SettingsUpdateAgent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AgentManager {

    public enum Type {
        SETTINGS_UPDATE
    }

    private Mainframe mainframe;
    private List<Agent> agents = new ArrayList<>();
    private LinkedHashMap<Type, List<Agent>> agentTypeMap = new LinkedHashMap<>();

    public AgentManager(Mainframe mainframe) {
        this.mainframe = mainframe;
        registerAgent(Type.SETTINGS_UPDATE, new SettingsUpdateAgent(mainframe));
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
