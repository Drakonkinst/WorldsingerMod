package io.github.drakonkinst.worldsinger.cosmere;

import io.github.drakonkinst.worldsinger.block.WaterReactiveBlock;
import io.github.drakonkinst.worldsinger.fluid.WaterReactiveFluid;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class WaterReactionManager {

    private static final int WATER_AMOUNT_STILL = 250;
    private static final int WATER_AMOUNT_FLOWING = 25;
    private static final int MAX_WATER_AMOUNT = 2500;
    private static final int MAX_ITERATIONS = 129;
    private static final int MAX_DEPTH = 32;

    public static void catalyzeAroundWater(World world, BlockPos waterPos) {
        List<Pair<BlockPos, WaterReactive>> reactiveBlocks = new ArrayList<>();
        int waterAmount = WaterReactionManager.absorbWaterAndCollectReactives(world, waterPos,
                reactiveBlocks);
        if (waterAmount <= 0) {
            return;
        }

        int waterAmountPerReactive = waterAmount / reactiveBlocks.size();
        for (Pair<BlockPos, WaterReactive> pair : reactiveBlocks) {
            WaterReactive waterReactive = pair.right();
            BlockPos pos = pair.left();
            waterReactive.reactToWater(world, pos, waterAmountPerReactive);
        }
    }

    public static int absorbWaterAndCollectReactives(World world, BlockPos centerPos,
            @Nullable List<Pair<BlockPos, WaterReactive>> reactiveBlocks) {
        Queue<IntObjectPair<BlockPos>> queue = new ArrayDeque<>();
        LongSet visited = new LongOpenHashSet();
        queue.add(IntObjectPair.of(0, centerPos));

        int numIterations = 0;
        int totalWaterAmount = 0;

        boolean shouldConsumeWater = true;
        while (!queue.isEmpty()) {
            IntObjectPair<BlockPos> next = queue.poll();
            BlockPos pos = next.right();
            long posId = pos.asLong();
            if (!visited.add(posId)) {
                continue;
            }

            int waterAmount = WaterReactionManager.absorbWaterAtBlock(world, pos);
            if (waterAmount <= 0) {
                BlockState blockState = world.getBlockState(pos);
                if (reactiveBlocks != null) {
                    // Check if water reactive
                    if (blockState.getBlock() instanceof WaterReactiveBlock waterReactiveBlock
                            && waterReactiveBlock.canReactToWater(pos, blockState)) {
                        reactiveBlocks.add(Pair.of(pos, waterReactiveBlock));
                    } else if (blockState.getFluidState()
                            .getFluid() instanceof WaterReactiveFluid waterReactiveFluid) {
                        reactiveBlocks.add(Pair.of(pos, waterReactiveFluid));
                    }
                }
            } else if (shouldConsumeWater) {
                // Absorb water
                totalWaterAmount += waterAmount;
                shouldConsumeWater = totalWaterAmount < MAX_WATER_AMOUNT;
                int depth = next.leftInt();
                if (depth >= MAX_DEPTH) {
                    continue;
                }
                for (Direction direction : ModConstants.CARDINAL_DIRECTIONS) {
                    queue.add(IntObjectPair.of(depth + 1, pos.offset(direction)));
                }
            }
            if (++numIterations >= MAX_ITERATIONS) {
                break;
            }
        }

        return Math.min(totalWaterAmount, MAX_WATER_AMOUNT);
    }

    public static int absorbWaterAtBlock(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        FluidState fluidState = world.getFluidState(pos);
        if (!fluidState.isIn(FluidTags.WATER)) {
            return 0;
        }
        Block block = blockState.getBlock();
        if (block instanceof FluidDrainable && !((FluidDrainable) block).tryDrainFluid(null, world,
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
            BlockEntity blockEntity =
                    blockState.hasBlockEntity() ? world.getBlockEntity(pos) : null;
            Block.dropStacks(blockState, world, pos, blockEntity);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            return WATER_AMOUNT_STILL;
        }
        // Not sure what kind of block
        return WATER_AMOUNT_FLOWING;
    }

    private WaterReactionManager() {}
}
