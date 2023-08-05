package io.github.drakonkinst.examplemod.block;

import io.github.drakonkinst.examplemod.fluid.ModFluids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;

public class AetherSporeFluidBlock extends FluidBlock {

    private static boolean isSolidAetherSpores(BlockState state) {
        return state.isIn(ModBlockTags.AETHER_SPORE_BLOCKS);
    }

    private static boolean isLiquidAetherSpores(BlockState state) {
        return state.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)
                && state.getFluidState().getLevel() >= 8
                &&
                state.getFluidState().isStill();
    }

    private static boolean shouldFluidize(BlockState fluidizedSource) {
        return fluidizedSource.isOf(Blocks.MAGMA_BLOCK) || isLiquidAetherSpores(fluidizedSource);
    }

    // Returns true if it should keep going
    private static boolean updateFluidization(WorldAccess world, BlockPos.Mutable pos,
            BlockState blockState,
            boolean fluidized) {
        if (AetherSporeFluidBlock.isSolidAetherSpores(blockState) &&
                blockState.getBlock() instanceof AetherSporeBlock aetherSporeBlock) {
            if (fluidized) {
                if (!world.setBlockState(pos, aetherSporeBlock.getFluidizedState(),
                        Block.NOTIFY_LISTENERS)) {
                    return false;
                }
            }
            return true;
        } else if (AetherSporeFluidBlock.isLiquidAetherSpores(blockState) &&
                blockState.getBlock() instanceof AetherSporeFluidBlock aetherSporeFluidBlock) {
            if (!fluidized) {
                if (!world.setBlockState(pos, aetherSporeFluidBlock.getSolidState(),
                        Block.NOTIFY_LISTENERS)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static void update(WorldAccess world, BlockPos pos, BlockState currentBlockState,
            BlockState fluidizedSource) {
        if (!AetherSporeFluidBlock.isLiquidAetherSpores(currentBlockState) &&
                !AetherSporeFluidBlock.isSolidAetherSpores(currentBlockState)) {
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
        world.scheduleFluidTick(pos, ModFluids.VERDANT_SPORES,
                ModFluids.VERDANT_SPORES.getTickRate(world));
        if (!state.canPlaceAt(world, pos) || direction == Direction.DOWN ||
                direction == Direction.UP && !neighborState.isIn(
                        ModBlockTags.AETHER_SPORE_SEA_BLOCKS) &&
                        AetherSporeFluidBlock.isSolidAetherSpores(neighborState)) {
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
