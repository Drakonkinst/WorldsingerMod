package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.util.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class CrimsonSpearBlock extends Block implements Waterloggable, SporeGrowthBlock {

    public enum Type implements StringIdentifiable {
        SINGLE,
        UPPER,
        LOWER;

        public String toString() {
            return this.asString();
        }

        @Override
        public String asString() {
            return this == UPPER ? "upper" : this == LOWER ? "lower" : "single";
        }
    }

    public CrimsonSpearBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(Properties.PERSISTENT, false)
                .with(Properties.WATERLOGGED, false)
                .with(ModProperties.CRIMSON_SPEAR_TYPE, Type.SINGLE));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(Properties.PERSISTENT, true)
                .with(Properties.WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(
                        Fluids.WATER));
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !state.get(Properties.PERSISTENT);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        // Decay over time
        if (SporeGrowthBlock.canDecay(world, pos, state, random)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false)
                : super.getFluidState(state);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.PERSISTENT, Properties.WATERLOGGED);
    }
}
