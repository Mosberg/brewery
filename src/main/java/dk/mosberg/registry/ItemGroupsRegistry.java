package dk.mosberg.registry;

import dk.mosberg.Brewery;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroupsRegistry {
    public static final ItemGroup BREWERY_GROUP = new ItemGroup(Brewery.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ItemsRegistry.BOTTLE);
        }
    };
    // Register additional item groups if needed
}
