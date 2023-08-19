package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractVerticalGrowthComponentBlock extends Block {

    public static final DirectionProperty GROWTH_DIRECTION = DirectionProperty.of(
            "growth_direction", Direction.UP, Direction.DOWN);

    public static Direction getGrowthDirection(BlockState state) {
        return state.get(GROWTH_DIRECTION);
    }

    protected final VoxelShape outlineShape;

    public AbstractVerticalGrowthComponentBlock(Settings settings, VoxelShape outlineShape) {
        super(settings);
        this.outlineShape = outlineShape;
        this.setDefaultState(this.getDefaultState().with(GROWTH_DIRECTION, Direction.UP));
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction growthDirection = ctx.getSide();
        if (growthDirection != Direction.UP && growthDirection != Direction.DOWN) {
            return null;
        }

        BlockState blockState = ctx.getWorld()
                .getBlockState(ctx.getBlockPos().offset(growthDirection));
        if (blockState.isOf(this.getStem()) || blockState.isOf(this.getPlant())) {
            return this.getPlant().getDefaultState().with(GROWTH_DIRECTION, growthDirection);
        }
        return this.getDefaultState().with(GROWTH_DIRECTION, growthDirection);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction growthDirection = AbstractVerticalGrowthComponentBlock.getGrowthDirection(state);
        BlockPos attachedBlockPos = pos.offset(growthDirection.getOpposite());
        BlockState attachedBlockState = world.getBlockState(attachedBlockPos);

        if (attachedBlockState.isSideSolidFullSquare(world, attachedBlockPos,
                growthDirection)) {
            return true;
        }
        if (attachedBlockState.isOf(this.getStem()) || attachedBlockState.isOf(this.getPlant())) {
            return growthDirection == attachedBlockState.get(GROWTH_DIRECTION);
        }
        return this.canAttachTo(state, attachedBlockState);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return this.outlineShape;
    }

    protected boolean canAttachTo(BlockState state, BlockState attachCandidate) {
        return false;
    }

    protected abstract Block getStem();

    protected abstract Block getPlant();

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(GROWTH_DIRECTION);
    }
}
