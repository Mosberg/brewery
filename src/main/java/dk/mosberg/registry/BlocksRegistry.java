package dk.mosberg.registry;

import dk.mosberg.Brewery;
import dk.mosberg.block.BarrelBlock;
import dk.mosberg.block.BoilerBlock;
import dk.mosberg.block.BottlerBlock;
import dk.mosberg.block.CrusherBlock;
import dk.mosberg.block.DistillerBlock;
import dk.mosberg.block.FermenterBlock;
import dk.mosberg.block.InfuserBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlocksRegistry {
    public static Block BARREL;
    public static Block BOILER;
    public static Block BOTTLER;
    public static Block CRUSHER;
    public static Block DISTILLER;
    public static Block FERMENTER;
    public static Block INFUSER;

    @SuppressWarnings("null")
    public static void register() {
        BARREL = Registry.register(Registries.BLOCK, Identifier.of(Brewery.MOD_ID, "barrel"),
                new BarrelBlock(Block.Settings.create()));
        BOILER = Registry.register(Registries.BLOCK, Identifier.of(Brewery.MOD_ID, "boiler"),
                new BoilerBlock(Block.Settings.create()));
        BOTTLER = Registry.register(Registries.BLOCK, Identifier.of(Brewery.MOD_ID, "bottler"),
                new BottlerBlock(Block.Settings.create()));
        CRUSHER = Registry.register(Registries.BLOCK, Identifier.of(Brewery.MOD_ID, "crusher"),
                new CrusherBlock(Block.Settings.create()));
        DISTILLER = Registry.register(Registries.BLOCK, Identifier.of(Brewery.MOD_ID, "distiller"),
                new DistillerBlock(Block.Settings.create()));
        FERMENTER = Registry.register(Registries.BLOCK, Identifier.of(Brewery.MOD_ID, "fermenter"),
                new FermenterBlock(Block.Settings.create()));
        INFUSER = Registry.register(Registries.BLOCK, Identifier.of(Brewery.MOD_ID, "infuser"),
                new InfuserBlock(Block.Settings.create()));
    }
}
