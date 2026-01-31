package dk.mosberg.registry;

import dk.mosberg.Brewery;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ItemsRegistry {
    public static Item BOTTLE;
    // Add other item fields here

    @SuppressWarnings("null")
    public static void register() {
        BOTTLE = Registry.register(Registries.ITEM, Identifier.of(Brewery.MOD_ID, "bottle"),
                new Item(new Item.Settings()));
        // Register other items here
    }
}
