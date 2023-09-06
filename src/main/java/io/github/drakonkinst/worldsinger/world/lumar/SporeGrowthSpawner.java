package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.block.WaterReactiveBlock;
import io.github.drakonkinst.worldsinger.component.SporeGrowthComponent;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.entity.VerdantSporeGrowthEntity;
import io.github.drakonkinst.worldsinger.fluid.WaterReactiveFluid;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.WaterReactive;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class SporeGrowthSpawner {

    private static final int WATER_AMOUNT_STILL = 250;
    private static final int WATER_AMOUNT_FLOWING = 25;
    private static final int MAX_WATER_AMOUNT = 2500;
    private static final double SAME_GROWTH_SEARCH_RADIUS = 3.0;
    private static final int MAX_ITERATIONS = 129;
    private static final int MAX_DEPTH = 32;

    public static void spawnVerdantSporeGrowth(World world, Vec3d pos, int spores, int water,
            boolean initialGrowth, boolean isSmall, boolean isSplit) {
        SporeGrowthSpawner.spawnVerdantSporeGrowth(world, pos, spores, water, initialGrowth,
                isSmall, isSplit, Int3.ZERO);
    }

    public static void spawnVerdantSporeGrowth(World world, Vec3d pos, int spores, int water,
            boolean initialGrowth, boolean isSmall, boolean isSplit, Int3 lastDir) {
        // If one already exists nearby, just augment that one
        if (!isSplit && SporeGrowthSpawner.checkForNearbyNewVerdantGrowth(world, pos, spores, water,
                initialGrowth, isSmall)) {
            return;
        }

        VerdantSporeGrowthEntity entity = ModEntityTypes.VERDANT_SPORE_GROWTH.create(world);
        if (entity == null) {
            return;
        }
        entity.setPosition(pos);
        entity.setSporeData(spores, water, initialGrowth);
        if (!lastDir.isZero()) {
            entity.setLastDir(lastDir);
        }
        if (isSmall) {
            entity.setInitialStage(VerdantSporeGrowthEntity.MAX_STAGE);
        }
        if (!isSplit) {
            ModConstants.LOGGER.info("SPAWNED");
        }

        world.spawnEntity(entity);
    }

    private static boolean checkForNearbyNewVerdantGrowth(World world, Vec3d pos, int spores,
            int water, boolean initialGrowth, boolean isSmall) {
        Box box = Box.from(pos).expand(SAME_GROWTH_SEARCH_RADIUS);
        List<VerdantSporeGrowthEntity> nearbySporeGrowthEntities = world
                .getEntitiesByClass(VerdantSporeGrowthEntity.class, box, sporeGrowthEntity -> {
                    SporeGrowthComponent sporeGrowthData = sporeGrowthEntity.getSporeGrowthData();
                    return sporeGrowthData.getAge() == 0
                            && sporeGrowthData.isInitialGrowth() == initialGrowth
                            && (sporeGrowthData.getStage() == 1) == isSmall;
                });
        if (nearbySporeGrowthEntities.isEmpty()) {
            return false;
        }
        VerdantSporeGrowthEntity existingSporeGrowthEntity = nearbySporeGrowthEntities.get(0);
        SporeGrowthComponent sporeGrowthData = existingSporeGrowthEntity.getSporeGrowthData();
        sporeGrowthData.setSpores(sporeGrowthData.getSpores() + spores);
        sporeGrowthData.setWater(sporeGrowthData.getWater() + water);
        ModConstants.LOGGER.info("COPIED AND INCREASED " + sporeGrowthData.getSpores() + " "
                + sporeGrowthData.getWater());
        return true;
    }

    public static void catalyzeAroundWater(World world, BlockPos waterPos) {
        int waterAmount = SporeGrowthSpawner.absorbWater(world, waterPos);
        if (waterAmount <= 0) {
            return;
        }

        ModConstants.LOGGER.info("WATER ABSORBED: " + waterAmount);

        // Check for other blocks that can be catalyzed by this block
        List<Pair<BlockPos, WaterReactive>> neighborReactives = new ArrayList<>(6);
        for (Direction direction : ModConstants.CARDINAL_DIRECTIONS) {
            BlockPos neighborPos = waterPos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof WaterReactiveBlock waterReactiveBlock
                    && waterReactiveBlock.canReactToWater(neighborPos, neighborState)) {
                neighborReactives.add(Pair.of(neighborPos, waterReactiveBlock));
            } else if (neighborState.getFluidState()
                    .getFluid() instanceof WaterReactiveFluid waterReactiveFluid) {
                neighborReactives.add(Pair.of(neighborPos, waterReactiveFluid));
            }
        }

        ModConstants.LOGGER.info("NEIGHBOR REACTIVES: " + neighborReactives.size());

        if (neighborReactives.isEmpty()) {
            return;
        }

        int waterAmountPerReactive = waterAmount / neighborReactives.size();
        for (Pair<BlockPos, WaterReactive> pair : neighborReactives) {
            WaterReactive waterReactive = pair.right();
            BlockPos pos = pair.left();
            waterReactive.reactToWater(world, pos, waterAmountPerReactive);
        }
    }

    private static int absorbWater(World world, BlockPos centerPos) {
        ArrayDeque<IntObjectPair<BlockPos>> queue = new ArrayDeque<>();
        LongOpenHashSet visited = new LongOpenHashSet();
        queue.add(IntObjectPair.of(0, centerPos));
        int numIterations = 0;
        int totalWaterAmount = 0;

        while (!queue.isEmpty()) {
            IntObjectPair<BlockPos> next = queue.poll();
            BlockPos pos = next.right();
            long posId = pos.asLong();
            if (!visited.add(posId)) {
                continue;
            }
            int waterAmount = SporeGrowthSpawner.absorbWaterAtBlock(world, pos);
            if (waterAmount <= 0) {
                continue;
            }
            totalWaterAmount += waterAmount;
            if (++numIterations >= MAX_ITERATIONS || totalWaterAmount >= MAX_WATER_AMOUNT) {
                return Math.min(totalWaterAmount, MAX_WATER_AMOUNT);
            }
            int depth = next.leftInt();
            if (depth >= MAX_DEPTH) {
                continue;
            }
            for (Direction direction : ModConstants.CARDINAL_DIRECTIONS) {
                queue.add(IntObjectPair.of(depth + 1, pos.offset(direction)));
            }
        }
        return totalWaterAmount;
    }

    private static int absorbWaterAtBlock(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        FluidState fluidState = world.getFluidState(pos);
        if (!fluidState.isIn(FluidTags.WATER)) {
            return 0;
        }
        Block block = blockState.getBlock();
        if (block instanceof FluidDrainable && !((FluidDrainable) block).tryDrainFluid(world,
                pos, blockState).isEmpty()) {
            // Full fluid block
            return WATER_AMOUNT_STILL;
        }
        if (blockState.getBlock() instanceof FluidBlock) {
            // Flowing block
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            return WATER_AMOUNT_FLOWING;
        } else if (blockState.isOf(Blocks.KELP) || blockState.isOf(Blocks.KELP_PLANT)
                || blockState.isOf(Blocks.SEAGRASS) || blockState.isOf(Blocks.TALL_SEAGRASS)) {
            // Waterlogged block
            BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(
                    pos) : null;
            Block.dropStacks(blockState, world, pos, blockEntity);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            return WATER_AMOUNT_STILL;
        }
        // Not sure what kind of block
        return WATER_AMOUNT_FLOWING;
    }

    private SporeGrowthSpawner() {}
}
