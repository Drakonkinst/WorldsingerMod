package io.github.drakonkinst.examplemod.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ConnectingBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class VerdantVineBranch extends ConnectingBlock implements Waterloggable {

    private static final float RADIUS = 0.3125f;
    private static final BooleanProperty[] DIRECTION_PROPERTIES = {DOWN, UP, NORTH, EAST, SOUTH,
            WEST};
    private static final Direction[] DIRECTIONS = {Direction.DOWN, Direction.UP, Direction.NORTH,
            Direction.EAST, Direction.SOUTH, Direction.WEST};

    private static List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();

        // Add in same order as ORDERED_DIRECTIONS
        neighbors.add(pos.down());
        neighbors.add(pos.up());
        neighbors.add(pos.north());
        neighbors.add(pos.east());
        neighbors.add(pos.south());
        neighbors.add(pos.west());

        return neighbors;
    }

    private static boolean canConnect(BlockView world, BlockPos pos, BlockState state,
            Direction direction) {
        boolean faceFullSquare = state.isSideSolidFullSquare(world, pos, direction.getOpposite());
        return state.isOf(ModBlocks.VERDANT_VINE_BRANCH) || faceFullSquare;
    }

    public VerdantVineBranch(Settings settings) {
        super(RADIUS, settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(NORTH, false)
                .with(SOUTH, false)
                .with(EAST, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(Properties.WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, Properties.WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.withConnectionProperties(ctx.getWorld(), ctx.getBlockPos());
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
        ChunkPos chunkPos = world.getChunk(neighborPos).getPos();
        BlockView blockView = world.getChunkAsView(chunkPos.x, chunkPos.z);
        boolean canConnect = canConnect(blockView, neighborPos, neighborState,
                direction.getOpposite());
        return state.with(FACING_PROPERTIES.get(direction), canConnect);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos,
            NavigationType type) {
        return false;
    }
}
