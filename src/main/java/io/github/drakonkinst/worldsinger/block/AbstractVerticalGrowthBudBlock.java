package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldAccess;

// Represents the outermost block of a vertical growth.
public abstract class AbstractVerticalGrowthBudBlock extends AbstractVerticalGrowthComponentBlock {

    public AbstractVerticalGrowthBudBlock(Settings settings, VoxelShape outlineShape) {
        super(settings, outlineShape);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {

        // If supporting block is no longer valid, break it
        Direction growthDirection = AbstractVerticalGrowthComponentBlock.getGrowthDirection(state);
        if (direction == growthDirection.getOpposite() && !state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }

        // If the same plant, no longer outermost block so turn into a stem
        if (direction == growthDirection && (this.isSamePlantWithDirection(neighborState,
                state.get(VERTICAL_DIRECTION)))) {
            return this.getStem().getStateWithProperties(state);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }

    @Override
    protected Block getBud() {
        return this;
    }
}
