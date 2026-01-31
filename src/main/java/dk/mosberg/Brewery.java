package dk.mosberg;

import dk.mosberg.loader.BreweryDataLoader;
import net.fabricmc.api.ModInitializer;

public class Brewery implements ModInitializer {
    public static final String MOD_ID = "brewery";

    @Override
    public void onInitialize() {
        BreweryDataLoader.register();
    }
}
