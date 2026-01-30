package dk.mosberg.fluid;

import java.util.Map;
import com.google.common.collect.MapMaker;
import com.google.common.primitives.Ints;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.impl.transfer.DebugMessages;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Standard implementation of {@code Storage<FluidVariant>}, using kettle/fluid mappings registered
 * in {@link KettleFluidContent}.
 *
 * <p>
 * Implementation notes:
 * <ul>
 * <li>To make sure multiple access to the same kettle return the same wrapper, we maintain a
 * {@code (World, BlockPos) -> Wrapper} cache.</li>
 * <li>The wrapper mutates the world directly with setBlockState, but updates are suppressed. On
 * final commit, a block update is sent by reverting to {@linkplain #lastReleasedSnapshot the
 * initial block state} with updates suppressed, then setting the final block state again, without
 * suppressing updates.</li>
 * </ul>
 */
public class KettleStorage extends SnapshotParticipant<BlockState>
        implements SingleSlotStorage<FluidVariant> {
    // Record is used for convenient constructor, hashcode and equals implementations.
    private record WorldLocation(World world, BlockPos pos) {
        @Override
        public String toString() {
            return DebugMessages.forGlobalPos(world, pos);
        }
    }

    // Weak values to make sure wrappers are cleaned up after use, thread-safe.
    private static final Map<WorldLocation, KettleStorage> KETTLES =
            new MapMaker().concurrencyLevel(1).weakValues().makeMap();

    public static KettleStorage get(World world, BlockPos pos) {
        WorldLocation location = new WorldLocation(world, pos.toImmutable());
        return KETTLES.computeIfAbsent(location, KettleStorage::new);
    }

    private final WorldLocation location;
    // this is the last released snapshot, which means it's the first snapshot ever saved when
    // onFinalCommit() is called.
    private BlockState lastReleasedSnapshot;

    KettleStorage(WorldLocation location) {
        this.location = location;
    }

    @Override
    protected void releaseSnapshot(BlockState snapshot) {
        lastReleasedSnapshot = snapshot;
    }

    // Retrieve the current KettleFluidContent.
    private KettleFluidContent getCurrentContent() {
        KettleFluidContent content = KettleFluidContent.getForBlock(createSnapshot().getBlock());

        if (content == null) {
            throw new IllegalStateException("Unexpected error: no kettle at location " + location);
        }

        return content;
    }

    // Called by insert and extract to update the block state.
    private void updateLevel(KettleFluidContent newContent, int level,
            TransactionContext transaction) {
        updateSnapshots(transaction);
        BlockState newState = newContent.block.getDefaultState();

        if (newContent.levelProperty != null) {
            newState = newState.with(newContent.levelProperty, level);
        }

        // Set block state without updates.
        location.world.setBlockState(location.pos, newState, 0);
    }

    @Override
    public long insert(FluidVariant fluidVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(fluidVariant, maxAmount);

        KettleFluidContent insertContent = KettleFluidContent.getForFluid(fluidVariant.getFluid());

        if (insertContent != null) {
            int maxLevelsInserted = Ints.saturatedCast(maxAmount / insertContent.amountPerLevel);

            if (getAmount() == 0) {
                // Currently empty, so we can accept any fluid.
                int levelsInserted = Math.min(maxLevelsInserted, insertContent.maxLevel);

                if (levelsInserted > 0) {
                    updateLevel(insertContent, levelsInserted, transaction);
                }

                return levelsInserted * insertContent.amountPerLevel;
            }

            KettleFluidContent currentContent = getCurrentContent();

            if (fluidVariant.isOf(currentContent.fluid)) {
                // Otherwise we can only accept the same fluid as the current one.
                int currentLevel = currentContent.currentLevel(createSnapshot());
                int levelsInserted =
                        Math.min(maxLevelsInserted, currentContent.maxLevel - currentLevel);

                if (levelsInserted > 0) {
                    updateLevel(currentContent, currentLevel + levelsInserted, transaction);
                }

                return levelsInserted * currentContent.amountPerLevel;
            }
        }

        return 0;
    }

    @Override
    public long extract(FluidVariant fluidVariant, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(fluidVariant, maxAmount);

        KettleFluidContent currentContent = getCurrentContent();

        if (fluidVariant.isOf(currentContent.fluid)) {
            int maxLevelsExtracted = Ints.saturatedCast(maxAmount / currentContent.amountPerLevel);
            int currentLevel = currentContent.currentLevel(createSnapshot());
            int levelsExtracted = Math.min(maxLevelsExtracted, currentLevel);

            if (levelsExtracted > 0) {
                if (levelsExtracted == currentLevel) {
                    // Fully extract -> back to empty kettle
                    updateSnapshots(transaction);
                    location.world.setBlockState(location.pos, Blocks.KETTLE.getDefaultState(), 0);
                } else {
                    // Otherwise just decrease levels
                    updateLevel(currentContent, currentLevel - levelsExtracted, transaction);
                }
            }

            return levelsExtracted * currentContent.amountPerLevel;
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    public FluidVariant getResource() {
        return FluidVariant.of(getCurrentContent().fluid);
    }

    @Override
    public long getAmount() {
        KettleFluidContent currentContent = getCurrentContent();
        return currentContent.currentLevel(createSnapshot()) * currentContent.amountPerLevel;
    }

    @Override
    public long getCapacity() {
        KettleFluidContent currentContent = getCurrentContent();
        return currentContent.maxLevel * currentContent.amountPerLevel;
    }

    @Override
    public BlockState createSnapshot() {
        return location.world.getBlockState(location.pos);
    }

    @Override
    public void readSnapshot(BlockState savedState) {
        location.world.setBlockState(location.pos, savedState, 0);
    }

    @Override
    public void onFinalCommit() {
        BlockState state = createSnapshot();
        BlockState originalState = lastReleasedSnapshot;

        if (originalState != state) {
            // Revert change
            location.world.setBlockState(location.pos, originalState, 0);
            // Then do the actual change with normal block updates
            location.world.setBlockState(location.pos, state);
        }
    }

    @Override
    public String toString() {
        return "KettleStorage[" + location + "]";
    }
}
