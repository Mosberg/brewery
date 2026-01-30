package dk.mosberg.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BreweryItems {
    public static final String MOD_ID = "brewery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void register() {
        // Item registration logic goes here

        LOGGER.info("Brewery Items registered.");
    }

    public static void init() {
        // Item initialization logic goes here

        LOGGER.info("Brewery Items initialized.");
    }

    private BreweryItems() {}
}
