package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.LumarSeetheManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class VerdantVineBranchBlock extends ConnectingBlock implements Waterloggable {

    private static final BooleanProperty PERSISTENT = Properties.PERSISTENT;
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final float RADIUS = 0.25f;
    private static final BooleanProperty[] DIRECTION_PROPERTIES = {DOWN, UP, NORTH, EAST, SOUTH,
            WEST};
    private static final Direction[] DIRECTIONS = {Direction.DOWN, Direction.UP, Direction.NORTH,
            Direction.EAST, Direction.SOUTH, Direction.WEST};

    private static List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();

        for (Direction direction : DIRECTIONS) {
            neighbors.add(pos.offset(direction));
        }

        return neighbors;
    }

    private static boolean canConnect(BlockView world, BlockPos pos, BlockState state,
            Direction direction) {
        if (state.isOf(ModBlocks.VERDANT_VINE_BRANCH)) {
            return true;
        }
        if (state.isOf(ModBlocks.VERDANT_VINE_SNARE)) {
            Direction attachDirection = VerdantVineSnareBlock.getDirection(state).getOpposite();
            return attachDirection == direction;
        }
        if (state.isOf(ModBlocks.TWISTING_VERDANT_VINES) || state.isOf(
                ModBlocks.TWISTING_VERDANT_VINES_PLANT)) {
            Direction growthDirection = AbstractVerticalGrowthComponentBlock.getGrowthDirection(
                            state)
                    .getOpposite();
            return growthDirection == direction;
        }
        boolean faceFullSquare = state.isSideSolidFullSquare(world, pos, direction.getOpposite());
        return faceFullSquare;
    }

    public VerdantVineBranchBlock(Settings settings) {
        super(RADIUS, settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(WATERLOGGED, false)
                .with(PERSISTENT, false));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            // Snares and vines cannot support branches
            if (neighborState.isOf(ModBlocks.VERDANT_VINE_SNARE) || neighborState.isOf(
                    ModBlocks.TWISTING_VERDANT_VINES) || neighborState.isOf(
                    ModBlocks.TWISTING_VERDANT_VINES_PLANT)) {
                continue;
            }

            if (canConnect(world, neighborPos, neighborState, direction)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED, PERSISTENT);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.withConnectionProperties(ctx.getWorld(), ctx.getBlockPos())
                .with(PERSISTENT, true);
    }

    public BlockState withConnectionProperties(BlockView world, BlockPos pos) {
        List<BlockPos> neighbors = getNeighbors(pos);
        BlockState state = this.getDefaultState();
        for (int i = 0; i < DIRECTION_PROPERTIES.length; ++i) {
            BlockPos neighborPos = neighbors.get(i);
            BlockState neighborState = world.getBlockState(neighborPos);
            boolean canConnect = canConnect(world, neighborPos, neighborState, DIRECTIONS[i]);
            state = state.with(DIRECTION_PROPERTIES[i], canConnect);
        }
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.scheduleBlockTick(pos, this, 1);
        }

        boolean canConnect = canConnect(world, neighborPos, neighborState,
                direction.getOpposite());
        return state.with(FACING_PROPERTIES.get(direction), canConnect);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos,
            NavigationType type) {
        return false;
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
}
