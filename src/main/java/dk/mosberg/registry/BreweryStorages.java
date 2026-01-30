package dk.mosberg.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BreweryStorages {
    public static final String MOD_ID = "brewery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void register() {
        // Storage registration logic goes here

        LOGGER.info("Brewery Storages registered.");
    }

    public static void init() {
        // Storage initialization logic goes here

        LOGGER.info("Brewery Storages initialized.");
    }

    private BreweryStorages() {}
}
