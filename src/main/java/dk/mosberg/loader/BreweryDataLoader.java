package dk.mosberg.loader;

import dk.mosberg.Brewery;
import dk.mosberg.registry.AlcoholTypeRegistry;
import dk.mosberg.registry.LiquidRegistry;
import dk.mosberg.registry.MachineRecipeRegistry;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public final class BreweryDataLoader implements SimpleSynchronousResourceReloadListener {

    public static void register() {
        net.fabricmc.fabric.api.resource.ResourceManagerHelper
                .get(net.minecraft.resource.ResourceType.SERVER_DATA)
                .registerReloadListener(new BreweryDataLoader());
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Brewery.MOD_ID, "data_loader");
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
