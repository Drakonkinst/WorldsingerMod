package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;

public class AetherSporeFluidBlock extends FluidBlock {

    public static void update(WorldAccess world, BlockPos pos, BlockState currentBlockState,
            BlockState fluidizedSource) {
        if (!hasAnyAetherSporesSource(currentBlockState)) {
            return;
        }

        BlockPos.Mutable mutable = pos.mutableCopy();
        boolean fluidized = AetherSporeFluidBlock.shouldFluidize(fluidizedSource);
        AetherSporeFluidBlock.updateFluidization(world, mutable, currentBlockState, fluidized);
        while (true) {
            mutable.move(Direction.UP);
            BlockState blockStateAtPos = world.getBlockState(mutable);
            if (!updateFluidization(world, mutable, blockStateAtPos, fluidized)) {
                break;
            }
        }
    }

    // Returns true if it should keep going
    public static boolean updateFluidization(WorldAccess world, BlockPos pos,
            BlockState blockState,
            boolean fluidized) {
        if (AetherSporeFluidBlock.isAetherSporesSolid(blockState) &&
                blockState.getBlock() instanceof AetherSporeBlock aetherSporeBlock) {
            if (fluidized) {
                if (!world.setBlockState(pos, aetherSporeBlock.getFluidizedState(),
                        Block.NOTIFY_LISTENERS)) {
                    return false;
                }
            }
            return true;
        } else if (AetherSporeFluidBlock.isAetherSporesFluidSource(blockState) &&
                blockState.getBlock() instanceof AetherSporeFluidBlock aetherSporeFluidBlock) {
            if (!fluidized) {
                if (!world.setBlockState(pos, aetherSporeFluidBlock.getSolidState(),
                        Block.NOTIFY_LISTENERS)) {
                    return false;
                }
            }
            return true;
        } else if (AetherSporeFluidBlock.isAetherSporesFluidlogged(blockState)
                && blockState.getBlock() instanceof Waterloggable waterloggable) {
            if (!fluidized) {
                // TODO: Add particle event
                // TODO: Maybe place the block above?
                waterloggable.tryDrainFluid(world, pos, blockState);
            }
            return true;
        }
        return false;
    }

    private static boolean hasAnyAetherSporesSource(BlockState state) {
        return isAetherSporesSolid(state) || isAetherSporesFluidSource(state)
                || isAetherSporesFluidlogged(state);
    }

    private static boolean isAetherSporesSolid(BlockState state) {
        return state.isIn(ModBlockTags.AETHER_SPORE_BLOCKS);
    }

    private static boolean isAetherSporesFluidSource(BlockState state) {
        return state.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)
                && state.getFluidState().getLevel() >= 8
                &&
                state.getFluidState().isStill();
    }

    private static boolean isAetherSporesFluidlogged(BlockState state) {
        return state.getBlock() instanceof Waterloggable && state.getFluidState()
                .isIn(ModFluidTags.AETHER_SPORES);
    }

    public static boolean shouldFluidize(BlockState fluidizedSource) {
        return fluidizedSource.isOf(Blocks.MAGMA_BLOCK) || isAetherSporesFluidSource(
                fluidizedSource) || isAetherSporesFluidlogged(fluidizedSource);
    }

    private BlockState solidBlockState = null;

    public AetherSporeFluidBlock(FlowableFluid fluid, Settings settings) {
        super(fluid, settings);
    }

    public void setSolidBlockState(BlockState blockState) {
        this.solidBlockState = blockState;
    }

    public BlockState getSolidState() {
        return solidBlockState;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState,
            WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleFluidTick(pos, fluid, fluid.getTickRate(world));
        if (!state.canPlaceAt(world, pos) || direction == Direction.DOWN ||
                direction == Direction.UP && !neighborState.isIn(
                        ModBlockTags.AETHER_SPORE_SEA_BLOCKS) &&
                        AetherSporeFluidBlock.isAetherSporesSolid(neighborState)) {
            world.scheduleBlockTick(pos, this, 5);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        AetherSporeFluidBlock.update(world, pos, state, world.getBlockState(pos.down()));
    }
}
