package me.purox.devi.core.agents;

import me.purox.devi.core.Devi;

public abstract class Agent {

    private Devi devi;

    public Agent(Devi devi) {
        this.devi = devi;
    }

    void start() {
        devi.getLogger().log(getClass().getSimpleName() + " has started.");
    }

    abstract boolean isRunning();

    void stop() {
        devi.getLogger().log(getClass().getSimpleName() + " has stopped.");
    }

}
