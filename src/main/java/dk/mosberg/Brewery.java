package dk.mosberg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dk.mosberg.loader.BreweryDataLoader;
import net.fabricmc.api.ModInitializer;

public class Brewery implements ModInitializer {
    public static final String MOD_ID = "brewery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        BreweryDataLoader.register();
    }
}
