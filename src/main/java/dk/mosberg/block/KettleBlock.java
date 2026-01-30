package dk.mosberg.block;

import com.mojang.serialization.MapCodec;
import dk.mosberg.block.kettle.KettleBehavior;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractKettleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;

/**
 * An empty kettle block.
 */
public class KettleBlock extends AbstractKettleBlock {
    public static final MapCodec<KettleBlock> CODEC = createCodec(KettleBlock::new);
    private static final float FILL_WITH_RAIN_CHANCE = 0.05F;
    private static final float FILL_WITH_SNOW_CHANCE = 0.1F;

    @Override
    public MapCodec<KettleBlock> getCodec() {
        return CODEC;
    }

    public KettleBlock(AbstractBlock.Settings settings) {
        super(settings, KettleBehavior.EMPTY_KETTLE_BEHAVIOR);
    }

    @Override
    public boolean isFull(BlockState state) {
        return false;
    }

    protected static boolean canFillWithPrecipitation(World world,
            Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN) {
            return world.getRandom().nextFloat() < 0.05F;
        } else {
            return precipitation == Biome.Precipitation.SNOW ? world.getRandom().nextFloat() < 0.1F
                    : false;
        }
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos,
            Biome.Precipitation precipitation) {
        if (canFillWithPrecipitation(world, precipitation)) {
            if (precipitation == Biome.Precipitation.RAIN) {
                world.setBlockState(pos, Blocks.WATER_KETTLE.getDefaultState());
                world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            } else if (precipitation == Biome.Precipitation.SNOW) {
                world.setBlockState(pos, Blocks.ALCOHOL_KETTLE.getDefaultState());
                world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            }
        }
    }

    @Override
    protected boolean canBeFilledByDripstone(Fluid fluid) {
        return true;
    }

    @Override
    protected void fillFromDripstone(BlockState state, World world, BlockPos pos, Fluid fluid) {
        if (fluid == Fluids.WATER) {
            BlockState blockState = Blocks.WATER_KETTLE.getDefaultState();
            world.setBlockState(pos, blockState);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
            world.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS_WATER_INTO_KETTLE, pos, 0);
        } else if (fluid == Fluids.LAVA) {
            BlockState blockState = Blocks.LAVA_KETTLE.getDefaultState();
            world.setBlockState(pos, blockState);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
            world.syncWorldEvent(WorldEvents.POINTED_DRIPSTONE_DRIPS_LAVA_INTO_KETTLE, pos, 0);
        }
    }
}
