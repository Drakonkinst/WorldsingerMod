package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidDrainable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public final class SunlightSporeManager {

    private static final int MAX_SUNLIGHT_SPREAD_DEPTH = 5;

    // Create light and heat
    public static void doSunlightSporeReaction(World world, BlockPos pos, int waterAmount,
            Random random) {
        int numBlocksAffected = SunlightSporeManager.spreadSunlightBlocks(world, pos,
                waterAmount, random);
        // TODO: Generate fire and other stuff
    }

    // Spread Sunlight blocks using BFS, replacing Sunlight Spore Sea / Sunlight Spore Blocks
    // Places at least one Sunlight block
    public static int spreadSunlightBlocks(World world, BlockPos startPos, int waterAmount,
            Random random) {
        // Number of blocks of Sunlight generated depends on amount of water
        int maxBlocks = waterAmount / 15;
        int numBlocksAffected = 0;

        // Always generate one block of Sunlight at the point of interaction
        BlockState blockState = world.getBlockState(startPos);
        if (blockState.isOf(ModBlocks.SUNLIGHT_SPORE_SEA) || blockState.isOf(
                ModBlocks.SUNLIGHT_SPORE_BLOCK) || blockState.isOf(Blocks.AIR)) {
            // Not a waterlogged block
            world.setBlockState(startPos, ModBlocks.SUNLIGHT.getDefaultState());
            numBlocksAffected++;
        }
        if (numBlocksAffected >= maxBlocks) {
            return numBlocksAffected;
        }

        LongSet visited = new LongOpenHashSet();
        Queue<IntObjectPair<BlockPos>> queue = new ArrayDeque<>();
        queue.add(new IntObjectImmutablePair<>(0, startPos));
        visited.add(startPos.asLong());

        while (!queue.isEmpty() && numBlocksAffected < maxBlocks) {
            IntObjectPair<BlockPos> nextPair = queue.remove();
            int depth = nextPair.leftInt();
            BlockPos nextPos = nextPair.right();
            BlockState state = world.getBlockState(nextPos);
            if (SunlightSporeManager.canSunlightReplace(state)) {
                // Replace with Sunlight
                world.setBlockState(nextPos, ModBlocks.SUNLIGHT.getDefaultState());
                numBlocksAffected++;
            } else if (state.getFluidState().isOf(ModFluids.SUNLIGHT_SPORES)
                    && state.getBlock() instanceof FluidDrainable fluidDrainable) {
                fluidDrainable.tryDrainFluid(null, world, nextPos, state);
            } else if (!SunlightSporeManager.canSunlightPassThrough(state)) {
                continue;
            }

            if (depth >= MAX_SUNLIGHT_SPREAD_DEPTH) {
                continue;
            }

            // Add neighbors
            for (Direction direction : Direction.shuffle(random)) {
                BlockPos neighborPos = nextPos.add(direction.getOffsetX(), direction.getOffsetY(),
                        direction.getOffsetZ());
                long encodedNeighborPos = neighborPos.asLong();
                if (visited.add(encodedNeighborPos)) {
                    queue.add(new IntObjectImmutablePair<>(depth + 1, neighborPos));
                }
            }
        }
        return numBlocksAffected;
    }

    private static boolean canSunlightReplace(BlockState state) {
        return state.isOf(ModBlocks.SUNLIGHT_SPORE_SEA) || state.isOf(
                ModBlocks.SUNLIGHT_SPORE_BLOCK);
    }

    private static boolean canSunlightPassThrough(BlockState state) {
        return state.isOf(ModBlocks.SUNLIGHT) || state.getFluidState()
                .isOf(ModFluids.SUNLIGHT_SPORES);
    }

    private SunlightSporeManager() {}
}
