package dk.mosberg.registry;

import dk.mosberg.Brewery;
import net.minecraft.block.Block;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlocksRegistry {
    public static Block CRUSHER;
    // Add other block fields here

    public static void register() {
        CRUSHER = Registry.register(Registry.BLOCK, new Identifier(Brewery.MOD_ID, "crusher"),
                new Block(Block.Settings.create()));
        // Register other blocks here
    }
}
