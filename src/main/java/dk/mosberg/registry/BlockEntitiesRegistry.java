package dk.mosberg.registry;

import dk.mosberg.Brewery;
import dk.mosberg.block.BarrelBlock;
import dk.mosberg.block.BoilerBlock;
import dk.mosberg.block.BottlerBlock;
import dk.mosberg.block.CrusherBlock;
import dk.mosberg.block.DistillerBlock;
import dk.mosberg.block.FermenterBlock;
import dk.mosberg.block.InfuserBlock;
import dk.mosberg.machine.impl.BarrelBlockEntity;
import dk.mosberg.machine.impl.BoilerBlockEntity;
import dk.mosberg.machine.impl.BottlerBlockEntity;
import dk.mosberg.machine.impl.CrusherBlockEntity;
import dk.mosberg.machine.impl.DistillerBlockEntity;
import dk.mosberg.machine.impl.FermenterBlockEntity;
import dk.mosberg.machine.impl.InfuserBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockEntitiesRegistry {
    public static BlockEntityType<BarrelBlockEntity> BARREL;
    public static BlockEntityType<BoilerBlockEntity> BOILER;
    public static BlockEntityType<BottlerBlockEntity> BOTTLER;
    public static BlockEntityType<CrusherBlockEntity> CRUSHER;
    public static BlockEntityType<DistillerBlockEntity> DISTILLER;
    public static BlockEntityType<FermenterBlockEntity> FERMENTER;
    public static BlockEntityType<InfuserBlockEntity> INFUSER;

    public static void register() {
        BARREL = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(Brewery.MOD_ID, "barrel"),
                BlockEntityType.Builder
                        .create(BarrelBlockEntity::new, new BarrelBlock(Block.Settings.create()))
                        .build(null));
        BOILER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(Brewery.MOD_ID, "boiler"),
                BlockEntityType.Builder
                        .create(BoilerBlockEntity::new, new BoilerBlock(Block.Settings.create()))
                        .build(null));
        BOTTLER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(Brewery.MOD_ID, "bottler"),
                BlockEntityType.Builder
                        .create(BottlerBlockEntity::new, new BottlerBlock(Block.Settings.create()))
                        .build(null));
        CRUSHER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(Brewery.MOD_ID, "crusher"),
                BlockEntityType.Builder
                        .create(CrusherBlockEntity::new, new CrusherBlock(Block.Settings.create()))
                        .build(null));
        DISTILLER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(Brewery.MOD_ID, "distiller"),
                BlockEntityType.Builder.create(DistillerBlockEntity::new,
                        new DistillerBlock(Block.Settings.create())).build(null));
        FERMENTER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(Brewery.MOD_ID, "fermenter"),
                BlockEntityType.Builder.create(FermenterBlockEntity::new,
                        new FermenterBlock(Block.Settings.create())).build(null));
        INFUSER = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                new Identifier(Brewery.MOD_ID, "infuser"),
                BlockEntityType.Builder
                        .create(InfuserBlockEntity::new, new InfuserBlock(Block.Settings.create()))
                        .build(null));
    }
}
