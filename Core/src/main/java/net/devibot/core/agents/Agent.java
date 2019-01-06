package net.devibot.core.agents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Agent {

    private final Logger logger = LoggerFactory.getLogger(Agent.class);

    public void start() {
        logger.info(getClass().getSimpleName() + " has started");
    }

    public void stop() {
        logger.info(getClass().getSimpleName() + " has stopped");
    }

    public abstract boolean isRunning();

}
