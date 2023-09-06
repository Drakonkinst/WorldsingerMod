package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.LumarSeethe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class TwistingVerdantVineBlock extends AbstractVerticalGrowthBudBlock implements
        Waterloggable {

    private static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 15.0,
            12.0);

    public TwistingVerdantVineBlock(Settings settings) {
        super(settings, SHAPE);
        this.setDefaultState(this.getDefaultState()
                .with(Properties.PERSISTENT, false)
                .with(Properties.WATERLOGGED, false));
    }

    public static boolean canAttach(BlockState state, BlockState attachCandidate) {
        // Can attach to other branches
        if (attachCandidate.isIn(ModBlockTags.VERDANT_VINE_BRANCH)) {
            return true;
        }
        // Can attach to a snare block if direction matches
        if (attachCandidate.isIn(ModBlockTags.VERDANT_VINE_SNARE)) {
            return VerdantVineSnareBlock.getDirection(attachCandidate)
                    == AbstractVerticalGrowthComponentBlock.getGrowthDirection(state);
        }
        return false;
    }

    @Override
    protected boolean canAttachTo(BlockState state, BlockState attachCandidate) {
        return TwistingVerdantVineBlock.canAttach(state, attachCandidate);
    }

    @Override
    protected Block getStem() {
        return ModBlocks.DEAD_TWISTING_VERDANT_VINES_PLANT;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.PERSISTENT, Properties.WATERLOGGED);
        super.appendProperties(builder);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState placementState = super.getPlacementState(ctx);
        if (placementState != null) {
            placementState = placementState
                    .with(Properties.PERSISTENT, true)
                    .with(Properties.WATERLOGGED,
                            ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(
                                    Fluids.WATER));
        }
        return placementState;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false)
                : super.getFluidState(state);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !state.get(Properties.PERSISTENT);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        // Decay over time
        if (LumarSeethe.areSporesFluidized(world) && !state.get(Properties.PERSISTENT)
                && !world.isRaining() && world.isSkyVisible(pos.up())) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos).with(Properties.PERSISTENT, state.get(Properties.PERSISTENT));
    }
}
