

package dk.mosberg.loader;

import dk.mosberg.Brewery;
import dk.mosberg.registry.AlcoholTypeRegistry;
import dk.mosberg.registry.LiquidRegistry;
import dk.mosberg.registry.MachineRecipeRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;

public final class BreweryDataLoader implements SynchronousResourceReloader {
    public static void register() {
        ResourceLoader.get(ResourceType.SERVER_DATA).registerReloader(
                Identifier.of(Brewery.MOD_ID, "data_loader"), new BreweryDataLoader());
    }

    @Override
    public void reload(ResourceManager manager) {
        AlcoholTypeRegistry.clear();
        LiquidRegistry.clear();
        MachineRecipeRegistry.clear();
        AlcoholTypeParser.loadAll(manager);
        LiquidParser.loadAll(manager);
        MachineRecipeParser.loadAll(manager);
    }
}
