package dk.mosberg.registry;

import java.util.function.BiFunction;
import dk.mosberg.Brewery;
import dk.mosberg.machine.impl.BarrelBlockEntity;
import dk.mosberg.machine.impl.BoilerBlockEntity;
import dk.mosberg.machine.impl.BottlerBlockEntity;
import dk.mosberg.machine.impl.CrusherBlockEntity;
import dk.mosberg.machine.impl.DistillerBlockEntity;
import dk.mosberg.machine.impl.FermenterBlockEntity;
import dk.mosberg.machine.impl.InfuserBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BlockEntitiesRegistry {
    // Grouped by machine type for better organization
    public static final BlockEntityType<BarrelBlockEntity> BARREL =
            register("barrel", BarrelBlockEntity::new, BlocksRegistry.BARREL);
    public static final BlockEntityType<BoilerBlockEntity> BOILER =
            register("boiler", BoilerBlockEntity::new, BlocksRegistry.BOILER);
    public static final BlockEntityType<BottlerBlockEntity> BOTTLER =
            register("bottler", BottlerBlockEntity::new, BlocksRegistry.BOTTLER);
    public static final BlockEntityType<CrusherBlockEntity> CRUSHER =
            register("crusher", CrusherBlockEntity::new, BlocksRegistry.CRUSHER);
    public static final BlockEntityType<DistillerBlockEntity> DISTILLER =
            register("distiller", DistillerBlockEntity::new, BlocksRegistry.DISTILLER);
    public static final BlockEntityType<FermenterBlockEntity> FERMENTER =
            register("fermenter", FermenterBlockEntity::new, BlocksRegistry.FERMENTER);
    public static final BlockEntityType<InfuserBlockEntity> INFUSER =
            register("infuser", InfuserBlockEntity::new, BlocksRegistry.INFUSER);

    private static <T extends net.minecraft.block.entity.BlockEntity> BlockEntityType<T> register(
            String name, BiFunction<BlockPos, BlockState, T> factory,
            net.minecraft.block.Block block) {
        Identifier id = Identifier.of(Brewery.MOD_ID, name);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder
                .create((FabricBlockEntityTypeBuilder.Factory<T>) factory, block).build());
    }

    // Call this during mod initialization (e.g., in your main mod class)
    public static void registerAll() {
        // All fields are now automatically registered at class load time
        // No additional work needed here
    }
}
