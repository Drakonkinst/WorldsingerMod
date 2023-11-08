package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class SunlightSporeFluid extends LivingAetherSporeFluid {

    public SunlightSporeFluid() {
        super(AetherSporeType.SUNLIGHT);
    }

    @Override
    public Fluid getStill() {
        return ModFluids.SUNLIGHT_SPORES;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_SUNLIGHT_SPORES;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.SUNLIGHT_SPORES_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return ModBlocks.SUNLIGHT_SPORE_SEA.getDefaultState().with(Properties.LEVEL_15,
                getBlockStateLevel(fluidState));
    }

    @Override
    protected void doWaterReaction(World world, BlockPos pos, FluidState state, int sporeAmount,
            int waterAmount, Random random) {
        // TODO: Create light and heat
        // TODO: Should spread to multiple adjacent sunlight blocks
        // Number of blocks of Sunlight generated depends on amount of water
        int maxBlocks = waterAmount / 15;
        // Always generate one block of Sunlight at the point of interaction
        Worldsinger.LOGGER.info("BLOCKS: " + maxBlocks + " for WATER: " + waterAmount);

        BlockState blockState = world.getBlockState(pos);
        if (blockState.isOf(ModBlocks.SUNLIGHT_SPORE_SEA) || blockState.isOf(
                ModBlocks.SUNLIGHT_SPORE_BLOCK)) {
            // Not a waterlogged block
            world.setBlockState(pos, ModBlocks.SUNLIGHT.getDefaultState());
            maxBlocks -= 1;
        }
        if (maxBlocks > 0) {
            spreadSunlightBlocks(world, pos, maxBlocks, 5);
        }
    }

    // Spread Sunlight blocks using BFS, replacing Sunlight Spore Sea / Sunlight Spore Blocks
    // Places at least one Sunlight block
    private void spreadSunlightBlocks(World world, BlockPos startPos, int maxBlocks, int maxDepth) {
        LongSet visited = new LongOpenHashSet();
        Queue<IntObjectPair<BlockPos>> queue = new ArrayDeque<>();
        queue.add(new IntObjectImmutablePair<>(0, startPos));
        visited.add(startPos.asLong());

        int numBlocksChanged = 0;

        while (!queue.isEmpty() && numBlocksChanged < maxBlocks) {
            IntObjectPair<BlockPos> nextPair = queue.remove();
            int depth = nextPair.leftInt();
            BlockPos nextPos = nextPair.right();
            BlockState state = world.getBlockState(nextPos);
            if (canSunlightReplace(state)) {
                // Replace with Sunlight
                world.setBlockState(nextPos, ModBlocks.SUNLIGHT.getDefaultState());
                numBlocksChanged++;
            } else if (!canSunlightPassThrough(state)) {
                continue;
            }

            if (depth >= maxDepth) {
                continue;
            }

            // Add neighbors
            for (Direction direction : ModConstants.CARDINAL_DIRECTIONS) {
                BlockPos neighborPos = nextPos.add(direction.getOffsetX(), direction.getOffsetY(),
                        direction.getOffsetZ());
                long encodedNeighborPos = neighborPos.asLong();
                if (visited.add(encodedNeighborPos)) {
                    queue.add(new IntObjectImmutablePair<>(depth + 1, neighborPos));
                }
            }
        }
    }

    private boolean canSunlightReplace(BlockState state) {
        return state.isOf(ModBlocks.SUNLIGHT_SPORE_SEA) || state.isOf(
                ModBlocks.SUNLIGHT_SPORE_BLOCK);
    }

    private boolean canSunlightPassThrough(BlockState state) {
        return state.isOf(ModBlocks.SUNLIGHT) || state.getFluidState()
                .isOf(ModFluids.SUNLIGHT_SPORES);
    }

    public static class Flowing extends SunlightSporeFluid {

        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends SunlightSporeFluid {

        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}
