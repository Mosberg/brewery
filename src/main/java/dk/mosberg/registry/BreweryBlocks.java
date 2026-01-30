package dk.mosberg.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BreweryBlocks {
    public static final String MOD_ID = "brewery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void register() {
        // Block registration logic goes here

        LOGGER.info("Brewery Blocks registered.");
    }

    public static void init() {
        // Block initialization logic goes here

        LOGGER.info("Brewery Blocks initialized.");
    }

    private BreweryBlocks() {}
}
