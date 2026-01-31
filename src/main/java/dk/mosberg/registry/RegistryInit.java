package dk.mosberg.registry;

public class RegistryInit {
    public static void registerAll() {
        ItemsRegistry.register();
        BlocksRegistry.register();
        BlockEntitiesRegistry.registerAll();
        MachineRegistry.clear();
        AlcoholTypeRegistry.clear();
        LiquidRegistry.clear();
        MachineRecipeRegistry.clear();
        // Data-driven registries are loaded by BreweryDataLoader on resource reload
    }
}
