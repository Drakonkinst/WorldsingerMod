package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldAccess;

public abstract class AbstractVerticalGrowthStemBlock extends AbstractVerticalGrowthComponentBlock {

    public AbstractVerticalGrowthStemBlock(Settings settings,
            VoxelShape outlineShape) {
        super(settings, outlineShape);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        Direction growthDirection = AbstractVerticalGrowthComponentBlock.getGrowthDirection(state);
        if (direction == growthDirection.getOpposite() && !state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }
        if (direction == growthDirection && (neighborState.isOf(this) || neighborState.isOf(
                this.getPlant()))) {
            return this.getPlant().getDefaultState().with(VERTICAL_DIRECTION, growthDirection);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }

    @Override
    protected Block getStem() {
        return this;
    }
}
