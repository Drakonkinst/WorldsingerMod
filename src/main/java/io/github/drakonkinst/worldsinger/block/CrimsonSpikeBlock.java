package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.util.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.Thickness;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class CrimsonSpikeBlock extends Block implements Waterloggable {

    private static final double TIP_OFFSET = 5.0;
    private static final double BASE_OFFSET = 4.0;
    private static final double FRUSTUM_OFFSET = 3.0;
    private static final double MIDDLE_OFFSET = 2.0;
    private static final VoxelShape TIP_SHAPE_X = createAxisAlignedShape(Axis.X, TIP_OFFSET);
    private static final VoxelShape TIP_SHAPE_Y = createAxisAlignedShape(Axis.Y, TIP_OFFSET);
    private static final VoxelShape TIP_SHAPE_Z = createAxisAlignedShape(Axis.Z, TIP_OFFSET);
    private static final VoxelShape BASE_SHAPE_X = createAxisAlignedShape(Axis.X, BASE_OFFSET);
    private static final VoxelShape BASE_SHAPE_Y = createAxisAlignedShape(Axis.Y, BASE_OFFSET);
    private static final VoxelShape BASE_SHAPE_Z = createAxisAlignedShape(Axis.Z, BASE_OFFSET);
    private static final VoxelShape FRUSTUM_SHAPE_X = createAxisAlignedShape(Axis.X,
            FRUSTUM_OFFSET);
    private static final VoxelShape FRUSTUM_SHAPE_Y = createAxisAlignedShape(Axis.Y,
            FRUSTUM_OFFSET);
    private static final VoxelShape FRUSTUM_SHAPE_Z = createAxisAlignedShape(Axis.Z,
            FRUSTUM_OFFSET);
    private static final VoxelShape MIDDLE_SHAPE_X = createAxisAlignedShape(Axis.X, MIDDLE_OFFSET);
    private static final VoxelShape MIDDLE_SHAPE_Y = createAxisAlignedShape(Axis.Y, MIDDLE_OFFSET);
    private static final VoxelShape MIDDLE_SHAPE_Z = createAxisAlignedShape(Axis.Z, MIDDLE_OFFSET);

    // Conditional XZ offsetter that acts normally when on Y-axis, and is disabled on other axes.
    public static Offsetter getOffsetter() {
        return (state, world, pos) -> {
            if (state.get(Properties.FACING).getAxis() != Axis.Y) {
                return Vec3d.ZERO;
            }
            
            Block block = state.getBlock();
            long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
            float f = block.getMaxHorizontalModelOffset();
            double d = MathHelper.clamp(((double) ((float) (l & 0xFL) / 15.0f) - 0.5) * 0.5,
                    -f, f);
            double e = MathHelper.clamp(
                    ((double) ((float) (l >> 8 & 0xFL) / 15.0f) - 0.5) * 0.5,
                    -f, f);
            return new Vec3d(d, 0.0, e);
        };
    }

    private static VoxelShape createAxisAlignedShape(Axis axis, double offset) {
        switch(axis) {
            case Y -> {
                return Block.createCuboidShape(offset, 0.0, offset, 16.0 - offset, 16.0,
                        16.0 - offset);
            }
            case X -> {
                return Block.createCuboidShape(0.0, offset, offset, 16.0, 16.0 - offset,
                        16.0 - offset);
            }
            default -> {
                // Z
                return Block.createCuboidShape(offset, offset, 0.0, 16.0 - offset, 16.0 - offset,
                        16.0);
            }
        }
    }

    private static boolean isCrimsonSpikeFacingDirection(BlockState state,
            Direction direction) {
        return (state.isOf(ModBlocks.CRIMSON_SPIKE) || state.isOf(ModBlocks.DEAD_CRIMSON_SPIKE))
                && state.get(Properties.FACING) == direction;
    }

    private static boolean canPlaceAtWithDirection(WorldView world, BlockPos pos,
            Direction direction) {
        BlockPos anchorPos = pos.offset(direction.getOpposite());
        BlockState anchorState = world.getBlockState(anchorPos);
        return anchorState.isSideSolidFullSquare(world, anchorPos, direction)
                || CrimsonSpikeBlock.isCrimsonSpikeFacingDirection(anchorState, direction);
    }

    private static Thickness getThickness(WorldView world, BlockPos pos, Direction direction) {
        Direction direction2 = direction.getOpposite();
        BlockState blockState = world.getBlockState(pos.offset(direction));
        if (CrimsonSpikeBlock.isCrimsonSpikeFacingDirection(blockState, direction2)) {
            return Thickness.TIP;
        }
        if (!CrimsonSpikeBlock.isCrimsonSpikeFacingDirection(blockState, direction)) {
            return Thickness.TIP;
        }
        Thickness thickness = blockState.get(ModProperties.THICKNESS_NO_MERGE);
        if (thickness == Thickness.TIP) {
            return Thickness.FRUSTUM;
        }
        BlockState blockState2 = world.getBlockState(pos.offset(direction2));
        if (!CrimsonSpikeBlock.isCrimsonSpikeFacingDirection(blockState2, direction)) {
            return Thickness.BASE;
        }
        return Thickness.MIDDLE;
    }

    @Nullable
    private static Direction getDirectionToPlaceAt(WorldView world, BlockPos pos,
            Direction direction) {
        if (CrimsonSpikeBlock.canPlaceAtWithDirection(world, pos, direction)) {
            return direction;
        } else if (CrimsonSpikeBlock.canPlaceAtWithDirection(world, pos, direction.getOpposite())) {
            return direction.getOpposite();
        } else {
            return null;
        }
    }

    public CrimsonSpikeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(Properties.FACING, Direction.UP)
                .with(Properties.PERSISTENT, false)
                .with(Properties.WATERLOGGED, false)
                .with(ModProperties.THICKNESS_NO_MERGE, Thickness.TIP)
        );
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return CrimsonSpikeBlock.canPlaceAtWithDirection(world, pos,
                state.get(Properties.FACING));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction direction = ctx.getPlayerLookDirection();
        Direction placeDirection = CrimsonSpikeBlock.getDirectionToPlaceAt(world, pos,
                direction.getOpposite());

        if (placeDirection == null) {
            return null;
        }

        Thickness thickness = CrimsonSpikeBlock.getThickness(world, pos, placeDirection);
        if (thickness == null) {
            return null;
        }

        return this.getDefaultState()
                .with(Properties.FACING, placeDirection)
                .with(ModProperties.THICKNESS_NO_MERGE, thickness)
                .with(Properties.PERSISTENT, true)
                .with(Properties.WATERLOGGED,
                        ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(
                                Fluids.WATER));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!this.canPlaceAt(state, world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        // if (direction != Direction.UP && direction != Direction.DOWN) {
        //     return state;
        // }
        Direction attachDirection = state.get(Properties.FACING);
        if (attachDirection == Direction.DOWN && world.getBlockTickScheduler()
                .isQueued(pos, this)) {
            return state;
        }
        if (direction == attachDirection.getOpposite() && !this.canPlaceAt(state, world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
            return state;
        }
        Thickness thickness = CrimsonSpikeBlock.getThickness(world, pos, attachDirection);
        return state.with(ModProperties.THICKNESS_NO_MERGE, thickness);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false)
                : super.getFluidState(state);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        Thickness thickness = state.get(ModProperties.THICKNESS_NO_MERGE);
        Axis axis = state.get(Properties.FACING).getAxis();
        VoxelShape voxelShape;
        if (thickness == Thickness.TIP) {
            voxelShape = axis == Axis.Y ? TIP_SHAPE_Y : axis == Axis.X ? TIP_SHAPE_X : TIP_SHAPE_Z;
        } else if (thickness == Thickness.FRUSTUM) {
            voxelShape = axis == Axis.Y ? FRUSTUM_SHAPE_Y
                    : axis == Axis.X ? FRUSTUM_SHAPE_X : FRUSTUM_SHAPE_Z;
        } else {
            // Middle or Base
            voxelShape = axis == Axis.Y ? MIDDLE_SHAPE_Y
                    : axis == Axis.X ? MIDDLE_SHAPE_X : MIDDLE_SHAPE_Z;

        }
        if (axis == Axis.Y) {
            Vec3d modelOffset = state.getModelOffset(world, pos);
            return voxelShape.offset(modelOffset.x, 0.0, modelOffset.z);
        }
        return voxelShape;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public float getMaxHorizontalModelOffset() {
        return 0.125f;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(
                Properties.FACING,
                Properties.PERSISTENT,
                Properties.WATERLOGGED,
                ModProperties.THICKNESS_NO_MERGE
        );
    }
}
