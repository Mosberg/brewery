package dk.mosberg.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BreweryBehaviors {
    public static final String MOD_ID = "brewery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void register() {
        // Behavior registration logic goes here

        LOGGER.info("Brewery Behaviors registered.");
    }

    public static void init() {
        // Behavior initialization logic goes here

        LOGGER.info("Brewery Behaviors initialized.");
    }

    private BreweryBehaviors() {}
}
