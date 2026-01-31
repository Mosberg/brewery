package dk.mosberg.registry;

public class RegistryInit {
    public static void registerAll() {
        ItemsRegistry.register();
        BlocksRegistry.register();
        BlockEntitiesRegistry.register();
        MachineRegistry.clear();
        AlcoholTypeRegistry.clear();
        LiquidRegistry.clear();
        MachineRecipeRegistry.clear();
        // Data-driven registries are loaded by BreweryDataLoader on resource reload
    }
}
