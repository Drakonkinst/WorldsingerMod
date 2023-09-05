package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.LumarSeethe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
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
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class VerdantVineSnareBlock extends WallMountedBlock {

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

    public VerdantVineSnareBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH)
                .with(FACE, WallMountLocation.FLOOR).with(Properties.PERSISTENT, false));
    }

    public static Direction getDirection(BlockState state) {
        return WallMountedBlock.getDirection(state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (WallMountedBlock.canPlaceAt(world, pos,
                WallMountedBlock.getDirection(state).getOpposite())) {
            return true;
        }
        BlockState attachedBlockState = world.getBlockState(
                pos.offset(WallMountedBlock.getDirection(state).getOpposite()));
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
        builder.add(FACE, FACING, Properties.PERSISTENT);
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
                    .with(Properties.PERSISTENT, true);
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
        if (LumarSeethe.areSporesFluidized(world) && !state.get(Properties.PERSISTENT)) {
            world.breakBlock(pos, true);
        }
    }
}
