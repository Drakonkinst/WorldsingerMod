package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.util.VoxelShapeUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class VerdantVineSnareBlock extends WallMountedBlock implements Waterloggable,
        SporeGrowthBlock {

    private static final VoxelShape[] SHAPES = VoxelShapeUtil.createAxisAlignedShapes(2.0, 0.0);

    public VerdantVineSnareBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(FACE, BlockFace.FLOOR)
                .with(Properties.PERSISTENT, false)
                .with(Properties.WATERLOGGED, false));
    }

    public static Direction getDirection(BlockState state) {
        return WallMountedBlock.getDirection(state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction attachDirection = WallMountedBlock.getDirection(state).getOpposite();
        if (WallMountedBlock.canPlaceAt(world, pos, attachDirection)) {
            return true;
        }
        BlockState attachedBlockState = world.getBlockState(pos.offset(attachDirection));
        if (attachedBlockState.isIn(ModBlockTags.AETHER_SPORE_SEA_BLOCKS)) {
            return true;
        }
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
        entity.slowMovement(state, new Vec3d(0.5, 0.25f, 0.5));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACE, FACING, Properties.PERSISTENT, Properties.WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos,
            ShapeContext context) {
        Axis axis = WallMountedBlock.getDirection(state).getAxis();
        return SHAPES[axis.ordinal()];
    }

    @Nullable
    @Override
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
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }
}
