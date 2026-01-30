package dk.mosberg.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BreweryItemGroups {
    public static final String MOD_ID = "brewery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void register() {
        // Item group registration logic goes here

        LOGGER.info("Brewery Item groups registered.");
    }

    public static void init() {
        // Item group initialization logic goes here

        LOGGER.info("Brewery Item groups initialized.");
    }

    private BreweryItemGroups() {}
}
