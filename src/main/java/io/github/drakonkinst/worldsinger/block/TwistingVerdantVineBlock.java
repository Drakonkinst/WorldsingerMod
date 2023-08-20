package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.LumarSeetheManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class TwistingVerdantVineBlock extends AbstractVerticalGrowthStemBlock {

    private static final BooleanProperty PERSISTENT = Properties.PERSISTENT;
    public static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 15.0, 12.0);

    public static boolean canAttach(BlockState state, BlockState attachCandidate) {
        if (attachCandidate.isOf(ModBlocks.VERDANT_VINE_BRANCH)) {
            return true;
        }
        if (attachCandidate.isOf(ModBlocks.VERDANT_VINE_SNARE)) {
            return VerdantVineSnareBlock.getDirection(attachCandidate)
                    == AbstractVerticalGrowthComponentBlock.getGrowthDirection(state);
        }
        return false;
    }

    public TwistingVerdantVineBlock(Settings settings) {
        super(settings, SHAPE);
        this.setDefaultState(this.getDefaultState().with(PERSISTENT, false));
    }

    @Override
    protected boolean canAttachTo(BlockState state, BlockState attachCandidate) {
        return canAttach(state, attachCandidate);
    }

    @Override
    protected Block getPlant() {
        return ModBlocks.TWISTING_VERDANT_VINES_PLANT;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PERSISTENT);
        super.appendProperties(builder);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState placementState = super.getPlacementState(ctx);
        if (placementState != null) {
            placementState = placementState.with(PERSISTENT, true);
        }
        return placementState;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !state.get(PERSISTENT);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (LumarSeetheManager.areSporesFluidized(world) && !state.get(PERSISTENT)) {
            Block.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos).with(PERSISTENT, state.get(PERSISTENT));
    }
}
