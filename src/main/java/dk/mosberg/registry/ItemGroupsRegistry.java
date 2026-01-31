package dk.mosberg.registry;

import dk.mosberg.Brewery;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ItemGroupsRegistry {
    public static final RegistryKey<ItemGroup> BREWERY_GROUP_KEY = RegistryKey
            .of(Registries.ITEM_GROUP.getKey(), Identifier.of(Brewery.MOD_ID, "brewery"));
    // Register the group in your mod init if needed
}
