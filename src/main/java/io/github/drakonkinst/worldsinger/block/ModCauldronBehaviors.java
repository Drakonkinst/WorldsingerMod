package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.cauldron.CauldronBehavior.CauldronBehaviorMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.world.event.GameEvent;

public final class ModCauldronBehaviors {

    public static final CauldronBehaviorMap DEAD_SPORE_CAULDRON_BEHAVIOR = CauldronBehavior.createMap(
            "dead_spores");
    public static final CauldronBehaviorMap VERDANT_SPORE_CAULDRON_BEHAVIOR = CauldronBehavior.createMap(
            "verdant_spores");
    public static final CauldronBehaviorMap CRIMSON_SPORE_CAULDRON_BEHAVIOR = CauldronBehavior.createMap(
            "crimson_spores");
    public static final CauldronBehaviorMap ZEPHYR_SPORE_CAULDRON_BEHAVIOR = CauldronBehavior.createMap(
            "zephyr_spores");
    public static final CauldronBehaviorMap SUNLIGHT_SPORE_CAULDRON_BEHAVIOR = CauldronBehavior.createMap(
            "sunlight_spores");

    public static final CauldronBehavior FILL_WITH_DEAD_SPORES = new FillWithFluidCauldronBehavior(
            () -> ModBlocks.DEAD_SPORE_CAULDRON, ModSoundEvents.ITEM_BUCKET_EMPTY_AETHER_SPORE);
    public static final CauldronBehavior FILL_WITH_VERDANT_SPORES = new FillWithSporeFluidCauldronBehavior(
            () -> ModBlocks.VERDANT_SPORE_CAULDRON, () -> ModBlocks.DEAD_SPORE_CAULDRON);
    public static final CauldronBehavior FILL_WITH_CRIMSON_SPORES = new FillWithSporeFluidCauldronBehavior(
            () -> ModBlocks.CRIMSON_SPORE_CAULDRON, () -> ModBlocks.DEAD_SPORE_CAULDRON);
    public static final CauldronBehavior FILL_WITH_ZEPHYR_SPORES = new FillWithSporeFluidCauldronBehavior(
            () -> ModBlocks.ZEPHYR_SPORE_CAULDRON, () -> ModBlocks.DEAD_SPORE_CAULDRON);
    public static final CauldronBehavior FILL_WITH_SUNLIGHT_SPORES = new FillWithSporeFluidCauldronBehavior(
            () -> ModBlocks.SUNLIGHT_SPORE_CAULDRON, () -> ModBlocks.DEAD_SPORE_CAULDRON);

    private static final SoundEvent FILL_SPORE_BOTTLE_SOUND = SoundEvents.BLOCK_SAND_BREAK;
    private static final SoundEvent FILL_SPORE_BUCKET_SOUND = SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW;

    public static void register() {
        ModCauldronBehaviors.registerExtraBucketBehavior(CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR);
        ModCauldronBehaviors.registerExtraBucketBehavior(CauldronBehavior.WATER_CAULDRON_BEHAVIOR);
        ModCauldronBehaviors.registerExtraBucketBehavior(CauldronBehavior.LAVA_CAULDRON_BEHAVIOR);
        ModCauldronBehaviors.registerExtraBucketBehavior(
                CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR);
        ModCauldronBehaviors.registerSporeBucket(ModCauldronBehaviors.DEAD_SPORE_CAULDRON_BEHAVIOR,
                ModBlocks.DEAD_SPORE_CAULDRON, ModItems.DEAD_SPORES_BUCKET,
                ModItems.DEAD_SPORES_BOTTLE);
        ModCauldronBehaviors.registerSporeBucket(
                ModCauldronBehaviors.VERDANT_SPORE_CAULDRON_BEHAVIOR,
                ModBlocks.VERDANT_SPORE_CAULDRON, ModItems.VERDANT_SPORES_BUCKET,
                ModItems.VERDANT_SPORES_BOTTLE);
        ModCauldronBehaviors.registerSporeBucket(
                ModCauldronBehaviors.CRIMSON_SPORE_CAULDRON_BEHAVIOR,
                ModBlocks.CRIMSON_SPORE_CAULDRON, ModItems.CRIMSON_SPORES_BUCKET,
                ModItems.CRIMSON_SPORES_BOTTLE);
        ModCauldronBehaviors.registerSporeBucket(
                ModCauldronBehaviors.ZEPHYR_SPORE_CAULDRON_BEHAVIOR,
                ModBlocks.ZEPHYR_SPORE_CAULDRON, ModItems.ZEPHYR_SPORES_BUCKET,
                ModItems.ZEPHYR_SPORES_BOTTLE);
        ModCauldronBehaviors.registerSporeBucket(
                ModCauldronBehaviors.SUNLIGHT_SPORE_CAULDRON_BEHAVIOR,
                ModBlocks.SUNLIGHT_SPORE_CAULDRON, ModItems.SUNLIGHT_SPORES_BUCKET,
                ModItems.SUNLIGHT_SPORES_BOTTLE);
    }

    private static void registerExtraBucketBehavior(CauldronBehaviorMap behavior) {
        ModCauldronBehaviors.registerExtraBucketBehavior(behavior.map());
    }

    private static void registerSporeBucket(CauldronBehaviorMap behavior, Block cauldronBlock,
            Item bucketItem, Item bottledItem) {
        ModCauldronBehaviors.registerSporeBucket(behavior.map(), cauldronBlock, bucketItem,
                bottledItem);
    }

    private static void registerExtraBucketBehavior(Map<Item, CauldronBehavior> behavior) {
        behavior.put(ModItems.DEAD_SPORES_BUCKET, ModCauldronBehaviors.FILL_WITH_DEAD_SPORES);
        behavior.put(ModItems.VERDANT_SPORES_BUCKET, ModCauldronBehaviors.FILL_WITH_VERDANT_SPORES);
        behavior.put(ModItems.CRIMSON_SPORES_BUCKET, ModCauldronBehaviors.FILL_WITH_CRIMSON_SPORES);
        behavior.put(ModItems.ZEPHYR_SPORES_BUCKET, ModCauldronBehaviors.FILL_WITH_ZEPHYR_SPORES);
        behavior.put(ModItems.SUNLIGHT_SPORES_BUCKET,
                ModCauldronBehaviors.FILL_WITH_SUNLIGHT_SPORES);
    }

    private static void registerSporeBucket(Map<Item, CauldronBehavior> behavior,
            Block cauldronBlock, Item bucketItem, Item bottledItem) {
        CauldronBehavior.registerBucketBehavior(behavior);
        ModCauldronBehaviors.registerExtraBucketBehavior(behavior);
        CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.map()
                .put(bottledItem, (state, world, pos, player, hand, stack) -> {
                    if (!world.isClient) {
                        Item item = stack.getItem();
                        player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player,
                                new ItemStack(Items.GLASS_BOTTLE)));
                        player.incrementStat(Stats.USE_CAULDRON);
                        player.incrementStat(Stats.USED.getOrCreateStat(item));
                        world.setBlockState(pos, cauldronBlock.getDefaultState());
                        world.playSound(null, pos, FILL_SPORE_BOTTLE_SOUND, SoundCategory.BLOCKS,
                                1.0f, 1.0f);
                        world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
                    }
                    return ActionResult.success(world.isClient);
                });
        behavior.put(Items.BUCKET,
                (state, world, pos, player, hand, stack) -> CauldronBehavior.emptyCauldron(state,
                        world, pos, player, hand, stack, new ItemStack(bucketItem),
                        statex -> statex.get(LeveledCauldronBlock.LEVEL) == 3,
                        FILL_SPORE_BUCKET_SOUND));
        behavior.put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient) {
                Item item = stack.getItem();
                player.setStackInHand(hand,
                        ItemUsage.exchangeStack(stack, player, bottledItem.getDefaultStack()));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(item));
                LeveledCauldronBlock.decrementFluidLevel(state, world, pos);
                world.playSound(null, pos, FILL_SPORE_BOTTLE_SOUND, SoundCategory.BLOCKS, 1.0f,
                        1.0f);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            }
            return ActionResult.success(world.isClient);
        });
        behavior.put(bottledItem, (state, world, pos, player, hand, stack) -> {
            if (state.get(LeveledCauldronBlock.LEVEL) == 3) {
                return ActionResult.PASS;
            }
            if (!world.isClient) {
                player.setStackInHand(hand,
                        ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                world.setBlockState(pos, state.cycle(LeveledCauldronBlock.LEVEL));
                world.playSound(null, pos, FILL_SPORE_BOTTLE_SOUND, SoundCategory.BLOCKS, 1.0f,
                        1.0f);
                world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.success(world.isClient);
        });
    }

    private ModCauldronBehaviors() {}
}
