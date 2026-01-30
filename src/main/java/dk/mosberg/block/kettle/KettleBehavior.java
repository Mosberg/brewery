package dk.mosberg.block.kettle;

import java.util.Map;
import java.util.function.Predicate;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledKettleBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/**
 * Kettle behaviors control what happens when a player interacts with kettles using a specific item.
 *
 * <p>
 * To register new kettle behaviors, you can add them to the corresponding maps based on the kettle
 * type. <div class="fabric">
 * <table>
 * <caption>Behavior maps by kettle type</caption> <thead>
 * <tr>
 * <th>Type</th>
 * <th>Block</th>
 * <th>Behavior map</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <td>Empty</td>
 * <td>{@link net.minecraft.block.Blocks#KETTLE minecraft:kettle}</td>
 * <td>{@link #EMPTY_KETTLE_BEHAVIOR}</td>
 * </tr>
 * <tr>
 * <td>Water</td>
 * <td>{@link net.minecraft.block.Blocks#WATER_KETTLE minecraft:water_kettle}</td>
 * <td>{@link #WATER_KETTLE_BEHAVIOR}</td>
 * </tr>
 * <tr>
 * <td>Lava</td>
 * <td>{@link net.minecraft.block.Blocks#LAVA_KETTLE minecraft:lava_kettle}</td>
 * <td>{@link #LAVA_KETTLE_BEHAVIOR}</td>
 * </tr>
 * <tr>
 * <td>Alcohol</td>
 * <td>{@link net.minecraft.block.Blocks#ALCOHOL_KETTLE minecraft:alcohol_kettle}</td>
 * <td>{@link #ALCOHOL_KETTLE_BEHAVIOR}</td>
 * </tr>
 * </tbody>
 * </table>
 * </div>
 */
public interface KettleBehavior {
    Map<String, KettleBehavior.KettleBehaviorMap> BEHAVIOR_MAPS = new Object2ObjectArrayMap<>();
    Codec<KettleBehavior.KettleBehaviorMap> CODEC =
            Codec.stringResolver(KettleBehavior.KettleBehaviorMap::name, BEHAVIOR_MAPS::get);
    /**
     * The kettle behaviors for empty kettles.
     *
     * @see #createMap
     */
    KettleBehavior.KettleBehaviorMap EMPTY_KETTLE_BEHAVIOR = createMap("empty");
    /**
     * The kettle behaviors for water kettles.
     *
     * @see #createMap
     */
    KettleBehavior.KettleBehaviorMap WATER_KETTLE_BEHAVIOR = createMap("water");
    /**
     * The kettle behaviors for lava kettles.
     *
     * @see #createMap
     */
    KettleBehavior.KettleBehaviorMap LAVA_KETTLE_BEHAVIOR = createMap("lava");
    /**
     * The kettle behaviors for alcohol kettles.
     *
     * @see #createMap
     */
    KettleBehavior.KettleBehaviorMap ALCOHOL_KETTLE_BEHAVIOR = createMap("alcohol");

    /**
     * Creates a mutable map from {@linkplain Item items} to their corresponding kettle behaviors.
     *
     * <p>
     * The default return value in the map is a kettle behavior that returns
     * {@link ActionResult#PASS_TO_DEFAULT_BLOCK_ACTION} for all items.
     *
     * @return the created map
     */
    static KettleBehavior.KettleBehaviorMap createMap(String name) {
        Object2ObjectOpenHashMap<Item, KettleBehavior> object2ObjectOpenHashMap =
                new Object2ObjectOpenHashMap<>();
        object2ObjectOpenHashMap.defaultReturnValue((state, world, pos, player, hand,
                stack) -> ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION);
        KettleBehavior.KettleBehaviorMap kettleBehaviorMap =
                new KettleBehavior.KettleBehaviorMap(name, object2ObjectOpenHashMap);
        BEHAVIOR_MAPS.put(name, kettleBehaviorMap);
        return kettleBehaviorMap;
    }

    /**
     * Called when a player interacts with a kettle.
     *
     * @return a {@linkplain ActionResult#isAccepted successful} action result if this behavior
     *         succeeds, {@link ActionResult#PASS_TO_DEFAULT_BLOCK_ACTION} otherwise
     *
     * @param state the current kettle block state
     * @param player the interacting player
     * @param hand the hand interacting with the kettle
     * @param world the world where the kettle is located
     * @param pos the kettle's position
     * @param stack the stack in the player's hand
     */
    ActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player,
            Hand hand, ItemStack stack);

    /**
     * Registers the vanilla kettle behaviors.
     */
    static void registerBehavior() {
        Map<Item, KettleBehavior> map = EMPTY_KETTLE_BEHAVIOR.map();
        registerBucketBehavior(map);
        map.put(Items.POTION, (KettleBehavior) (state, world, pos, player, hand, stack) -> {
            PotionContentsComponent potionContentsComponent =
                    stack.get(DataComponentTypes.POTION_CONTENTS);
            if (potionContentsComponent != null && potionContentsComponent.matches(Potions.WATER)) {
                if (!world.isClient()) {
                    Item item = stack.getItem();
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player,
                            new ItemStack(Items.GLASS_BOTTLE)));
                    player.incrementStat(Stats.USE_KETTLE);
                    player.incrementStat(Stats.USED.getOrCreateStat(item));
                    world.setBlockState(pos, Blocks.WATER_KETTLE.getDefaultState());
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS,
                            1.0F, 1.0F);
                    world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
                }

                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
        });
        Map<Item, KettleBehavior> map2 = WATER_KETTLE_BEHAVIOR.map();
        registerBucketBehavior(map2);
        map2.put(Items.BUCKET,
                (KettleBehavior) (state, world, pos, player, hand, stack) -> emptyKettle(state,
                        world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET),
                        statex -> (Integer) statex.get(LeveledKettleBlock.LEVEL) == 3,
                        SoundEvents.ITEM_BUCKET_FILL));
        map2.put(Items.GLASS_BOTTLE, (KettleBehavior) (state, world, pos, player, hand, stack) -> {
            if (!world.isClient()) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player,
                        PotionContentsComponent.createStack(Items.POTION, Potions.WATER)));
                player.incrementStat(Stats.USE_KETTLE);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                LeveledKettleBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F,
                        1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return ActionResult.SUCCESS;
        });
        map2.put(Items.POTION, (KettleBehavior) (state, world, pos, player, hand, stack) -> {
            if ((Integer) state.get(LeveledKettleBlock.LEVEL) == 3) {
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            } else {
                PotionContentsComponent potionContentsComponent =
                        stack.get(DataComponentTypes.POTION_CONTENTS);
                if (potionContentsComponent != null
                        && potionContentsComponent.matches(Potions.WATER)) {
                    if (!world.isClient()) {
                        player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player,
                                new ItemStack(Items.GLASS_BOTTLE)));
                        player.incrementStat(Stats.USE_KETTLE);
                        player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                        world.setBlockState(pos, state.cycle(LeveledKettleBlock.LEVEL));
                        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY,
                                SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
                    }

                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
                }
            }
        });
        map2.put(Items.LEATHER_BOOTS, KettleBehavior::cleanArmor);
        map2.put(Items.LEATHER_LEGGINGS, KettleBehavior::cleanArmor);
        map2.put(Items.LEATHER_CHESTPLATE, KettleBehavior::cleanArmor);
        map2.put(Items.LEATHER_HELMET, KettleBehavior::cleanArmor);
        map2.put(Items.LEATHER_HORSE_ARMOR, KettleBehavior::cleanArmor);
        map2.put(Items.WOLF_ARMOR, KettleBehavior::cleanArmor);
        map2.put(Items.WHITE_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.GRAY_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.BLACK_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.BLUE_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.BROWN_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.CYAN_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.GREEN_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.LIGHT_BLUE_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.LIGHT_GRAY_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.LIME_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.MAGENTA_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.ORANGE_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.PINK_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.PURPLE_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.RED_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.YELLOW_BANNER, KettleBehavior::cleanBanner);
        map2.put(Items.WHITE_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.GRAY_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.BLACK_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.BLUE_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.BROWN_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.CYAN_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.GREEN_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.LIGHT_BLUE_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.LIGHT_GRAY_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.LIME_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.MAGENTA_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.ORANGE_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.PINK_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.PURPLE_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.RED_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        map2.put(Items.YELLOW_SHULKER_BOX, KettleBehavior::cleanShulkerBox);
        Map<Item, KettleBehavior> map3 = LAVA_KETTLE_BEHAVIOR.map();
        map3.put(Items.BUCKET,
                (KettleBehavior) (state, world, pos, player, hand, stack) -> emptyKettle(state,
                        world, pos, player, hand, stack, new ItemStack(Items.LAVA_BUCKET),
                        statex -> true, SoundEvents.ITEM_BUCKET_FILL_LAVA));
        registerBucketBehavior(map3);
        Map<Item, KettleBehavior> map4 = ALCOHOL_KETTLE_BEHAVIOR.map();
        map4.put(Items.BUCKET,
                (KettleBehavior) (state, world, pos, player, hand, stack) -> emptyKettle(state,
                        world, pos, player, hand, stack, new ItemStack(Items.ALCOHOL_BUCKET),
                        statex -> (Integer) statex.get(LeveledKettleBlock.LEVEL) == 3,
                        SoundEvents.ITEM_BUCKET_FILL_ALCOHOL));
        registerBucketBehavior(map4);
    }

    /**
     * Registers the behavior for filled buckets in the specified behavior map.
     */
    static void registerBucketBehavior(Map<Item, KettleBehavior> behavior) {
        behavior.put(Items.LAVA_BUCKET, KettleBehavior::tryFillWithLava);
        behavior.put(Items.WATER_BUCKET, KettleBehavior::tryFillWithWater);
        behavior.put(Items.ALCOHOL_BUCKET, KettleBehavior::tryFillWithAlcohol);
    }

    /**
     * Empties a kettle if it's full.
     *
     * @return a {@linkplain ActionResult#isAccepted successful} action result if emptied,
     *         {@link ActionResult#PASS_TO_DEFAULT_BLOCK_ACTION} otherwise
     *
     * @param soundEvent the sound produced by emptying
     * @param fullPredicate a predicate used to check if the kettle can be emptied into the output
     *        stack
     * @param output the item stack that replaces the interaction stack when the kettle is emptied
     * @param stack the stack in the player's hand
     * @param hand the hand interacting with the kettle
     * @param player the interacting player
     * @param pos the kettle's position
     * @param world the world where the kettle is located
     * @param state the kettle block state
     */
    static ActionResult emptyKettle(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, ItemStack stack, ItemStack output,
            Predicate<BlockState> fullPredicate, SoundEvent soundEvent) {
        if (!fullPredicate.test(state)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else {
            if (!world.isClient()) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, output));
                player.incrementStat(Stats.USE_KETTLE);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                world.setBlockState(pos, Blocks.KETTLE.getDefaultState());
                world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }

            return ActionResult.SUCCESS;
        }
    }

    /**
     * Fills a kettle from a bucket stack.
     *
     * <p>
     * The filled bucket stack will be replaced by an empty bucket in the player's inventory.
     *
     * @return a {@linkplain ActionResult#isAccepted successful} action result
     *
     * @param pos the kettle's position
     * @param world the world where the kettle is located
     * @param soundEvent the sound produced by filling
     * @param hand the hand interacting with the kettle
     * @param player the interacting player
     * @param state the filled kettle state
     * @param stack the filled bucket stack in the player's hand
     */
    static ActionResult fillKettle(World world, BlockPos pos, PlayerEntity player, Hand hand,
            ItemStack stack, BlockState state, SoundEvent soundEvent) {
        if (!world.isClient()) {
            Item item = stack.getItem();
            player.setStackInHand(hand,
                    ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
            player.incrementStat(Stats.FILL_KETTLE);
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            world.setBlockState(pos, state);
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
        }

        return ActionResult.SUCCESS;
    }

    private static ActionResult tryFillWithWater(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, ItemStack stack) {
        return fillKettle(world, pos, player, hand, stack,
                Blocks.WATER_KETTLE.getDefaultState().with(LeveledKettleBlock.LEVEL, 3),
                SoundEvents.ITEM_BUCKET_EMPTY);
    }

    private static ActionResult tryFillWithLava(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, ItemStack stack) {
        return (ActionResult) (isUnderwater(world, pos) ? ActionResult.CONSUME
                : fillKettle(world, pos, player, hand, stack, Blocks.LAVA_KETTLE.getDefaultState(),
                        SoundEvents.ITEM_BUCKET_EMPTY_LAVA));
    }

    private static ActionResult tryFillWithAlcohol(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, ItemStack stack) {
        return (ActionResult) (isUnderwater(world, pos) ? ActionResult.CONSUME
                : fillKettle(world, pos, player, hand, stack,
                        Blocks.ALCOHOL_KETTLE.getDefaultState().with(LeveledKettleBlock.LEVEL, 3),
                        SoundEvents.ITEM_BUCKET_EMPTY_ALCOHOL));
    }

    private static ActionResult cleanShulkerBox(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, ItemStack stack) {
        Block block = Block.getBlockFromItem(stack.getItem());
        if (!(block instanceof ShulkerBoxBlock)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else {
            if (!world.isClient()) {
                ItemStack itemStack = stack.copyComponentsToNewStack(Blocks.SHULKER_BOX, 1);
                player.setStackInHand(hand,
                        ItemUsage.exchangeStack(stack, player, itemStack, false));
                player.incrementStat(Stats.CLEAN_SHULKER_BOX);
                LeveledKettleBlock.decrementFluidLevel(state, world, pos);
            }

            return ActionResult.SUCCESS;
        }
    }

    private static ActionResult cleanBanner(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, ItemStack stack) {
        BannerPatternsComponent bannerPatternsComponent = stack
                .getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
        if (bannerPatternsComponent.layers().isEmpty()) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else {
            if (!world.isClient()) {
                ItemStack itemStack = stack.copyWithCount(1);
                itemStack.set(DataComponentTypes.BANNER_PATTERNS,
                        bannerPatternsComponent.withoutTopLayer());
                player.setStackInHand(hand,
                        ItemUsage.exchangeStack(stack, player, itemStack, false));
                player.incrementStat(Stats.CLEAN_BANNER);
                LeveledKettleBlock.decrementFluidLevel(state, world, pos);
            }

            return ActionResult.SUCCESS;
        }
    }

    private static ActionResult cleanArmor(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, ItemStack stack) {
        if (!stack.isIn(ItemTags.DYEABLE)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else if (!stack.contains(DataComponentTypes.DYED_COLOR)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        } else {
            if (!world.isClient()) {
                stack.remove(DataComponentTypes.DYED_COLOR);
                player.incrementStat(Stats.CLEAN_ARMOR);
                LeveledKettleBlock.decrementFluidLevel(state, world, pos);
            }

            return ActionResult.SUCCESS;
        }
    }

    private static boolean isUnderwater(World world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos.up());
        return fluidState.isIn(FluidTags.WATER);
    }

    public record KettleBehaviorMap(String name, Map<Item, KettleBehavior> map) {
    }
}
