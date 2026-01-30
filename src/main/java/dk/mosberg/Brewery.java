package dk.mosberg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dk.mosberg.registry.BreweryBehaviors;
import dk.mosberg.registry.BreweryBlocks;
import dk.mosberg.registry.BreweryFluids;
import dk.mosberg.registry.BreweryItemGroups;
import dk.mosberg.registry.BreweryItems;
import dk.mosberg.registry.BreweryStorages;
import net.fabricmc.api.ModInitializer;

public class Brewery implements ModInitializer {
    public static final String MOD_ID = "brewery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        BreweryFluids.register();
        BreweryBlocks.register();
        BreweryItems.register();
        BreweryItemGroups.register();
        BreweryBehaviors.register();
        BreweryStorages.register();

        BreweryFluids.init();
        BreweryBlocks.init();
        BreweryItems.init();
        BreweryItemGroups.init();
        BreweryBehaviors.init();
        BreweryStorages.init();

        LOGGER.info("Brewery registered and initialized.");
    }
}
