package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeetheManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class VerdantVineSnareBlock extends WallMountedBlock implements Waterloggable {

    private static final double MIN_VERTICAL = 0.0;
    private static final double MAX_VERTICAL = 16.0;
    private static final double MIN_HORIZONTAL = 2.0;
    private static final double MAX_HORIZONTAL = 14.0;
    protected static final VoxelShape NORTH_SOUTH_SHAPE = Block.createCuboidShape(MIN_HORIZONTAL,
            MIN_HORIZONTAL,
            MIN_VERTICAL, MAX_HORIZONTAL, MAX_HORIZONTAL, MAX_VERTICAL);
    protected static final VoxelShape EAST_WEST_SHAPE = Block.createCuboidShape(MIN_VERTICAL,
            MIN_HORIZONTAL, MIN_HORIZONTAL,
            MAX_VERTICAL, MAX_HORIZONTAL, MAX_HORIZONTAL);
    protected static final VoxelShape FLOOR_CEILING_SHAPE = Block.createCuboidShape(MIN_HORIZONTAL,
            MIN_VERTICAL,
            MIN_HORIZONTAL, MAX_HORIZONTAL,
            MAX_VERTICAL,
            MAX_HORIZONTAL);

    public static Direction getDirection(BlockState state) {
        return WallMountedBlock.getDirection(state);
    }

    public VerdantVineSnareBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH)
                .with(FACE, WallMountLocation.FLOOR).with(Properties.PERSISTENT, false)
                .with(Properties.WATERLOGGED, false));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (world.getFluidState(pos).isIn(ModFluidTags.AETHER_SPORES) && !world.getFluidState(
                pos.up()).isIn(ModFluidTags.AETHER_SPORES)) {
            return true;
        }

        if (WallMountedBlock.canPlaceAt(world, pos,
                WallMountedBlock.getDirection(state).getOpposite())) {
            return true;
        }
        BlockState attachedBlockState = world.getBlockState(
                pos.offset(WallMountedBlock.getDirection(state).getOpposite()));
        if (attachedBlockState.isIn(ModBlockTags.VERDANT_VINE_BRANCH)) {
            return true;
        }
        if (attachedBlockState.isIn(ModBlockTags.VERDANT_VINE_SNARE)) {
            return WallMountedBlock.getDirection(attachedBlockState)
                    == WallMountedBlock.getDirection(state);
        }
        return false;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        entity.slowMovement(state, new Vec3d(0.25, 0.05f, 0.25));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, Properties.PERSISTENT, Properties.WATERLOGGED);
        super.appendProperties(builder);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        return switch(WallMountedBlock.getDirection(state)) {
            case NORTH, SOUTH -> NORTH_SOUTH_SHAPE;
            case EAST, WEST -> EAST_WEST_SHAPE;
            case DOWN, UP -> FLOOR_CEILING_SHAPE;
        };
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState placementState = super.getPlacementState(ctx);
        if (placementState != null) {
            placementState = placementState
                    .with(Properties.PERSISTENT, true)
                    .with(Properties.WATERLOGGED,
                            ctx.getWorld().getFluidState(ctx.getBlockPos()).getFluid()
                                    == Fluids.WATER);
            ;
        }
        return placementState;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false)
                : super.getFluidState(state);
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

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return !state.get(Properties.PERSISTENT);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (LumarSeetheManager.areSporesFluidized(world) && !state.get(Properties.PERSISTENT)) {
            Block.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }
}
