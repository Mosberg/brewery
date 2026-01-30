package dk.mosberg.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BreweryFluids {
    public static final String MOD_ID = "brewery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void register() {
        // Fluid registration logic goes here

        LOGGER.info("Brewery Fluids registered.");
    }

    public static void init() {
        // Fluid initialization logic goes here

        LOGGER.info("Brewery Fluids initialized.");
    }

    private BreweryFluids() {}
}
