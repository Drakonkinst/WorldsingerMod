package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

// Represents middle blocks of a vertical growth.
public abstract class AbstractVerticalGrowthStemBlock extends AbstractVerticalGrowthComponentBlock {

    public AbstractVerticalGrowthStemBlock(Settings settings, VoxelShape outlineShape) {
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

        // If now topmost, turn into bud
        if (direction == growthDirection && !this.isSamePlantWithDirection(neighborState,
                state.get(VERTICAL_DIRECTION))) {
            return this.getBud().getStateWithProperties(state);
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return new ItemStack(this.getBud());
    }

    @Override
    protected Block getStem() {
        return this;
    }
}
