package io.github.drakonkinst.worldsinger.world;

import io.github.drakonkinst.worldsinger.block.WaterReactiveBlock;
import io.github.drakonkinst.worldsinger.fluid.WaterReactiveFluid;
import io.github.drakonkinst.worldsinger.util.ModConstants;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public final class WaterReactionManager {

    private static final int WATER_AMOUNT_STILL = 250;
    private static final int WATER_AMOUNT_FLOWING = 25;
    private static final int MAX_WATER_AMOUNT = 1500;
    private static final int MAX_ITERATIONS = 129;
    private static final int MAX_DEPTH = 32;

    private static final Int3[] SURROUNDING_AND_CENTER = {Int3.ZERO, Int3.UP, Int3.DOWN, Int3.NORTH,
            Int3.SOUTH, Int3.EAST, Int3.WEST};

    private WaterReactionManager() {}

    public static void catalyzeAroundWater(World world, BlockPos waterPos) {
        int waterAmount = absorbWater(world, waterPos);
        if (waterAmount <= 0) {
            return;
        }

        // Check for other blocks that can be catalyzed by this block
        List<Pair<BlockPos, WaterReactive>> neighborReactives = new ArrayList<>(6);
        for (Int3 direction : SURROUNDING_AND_CENTER) {
            BlockPos neighborPos = waterPos.add(direction.x(), direction.y(), direction.z());
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof WaterReactiveBlock waterReactiveBlock
                    && waterReactiveBlock.canReactToWater(neighborPos, neighborState)) {
                neighborReactives.add(Pair.of(neighborPos, waterReactiveBlock));
            } else if (neighborState.getFluidState()
                    .getFluid() instanceof WaterReactiveFluid waterReactiveFluid) {
                neighborReactives.add(Pair.of(neighborPos, waterReactiveFluid));
            }
        }

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

    public static int absorbWater(World world, BlockPos centerPos) {
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
            int waterAmount = absorbWaterAtBlock(world, pos);
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
            BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(
                    pos) : null;
            Block.dropStacks(blockState, world, pos, blockEntity);
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            return WATER_AMOUNT_STILL;
        }
        // Not sure what kind of block
        return WATER_AMOUNT_FLOWING;
    }
}
