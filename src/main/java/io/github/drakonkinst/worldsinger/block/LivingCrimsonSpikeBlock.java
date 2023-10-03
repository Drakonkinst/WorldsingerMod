package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.util.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LivingCrimsonSpikeBlock extends CrimsonSpikeBlock implements SporeKillable,
        WaterReactiveBlock {

    public LivingCrimsonSpikeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(ModProperties.CATALYZED, false));
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (!state.get(ModProperties.CATALYZED) && world.hasRain(pos.up())) {
            this.reactToWater(world, pos, state, Integer.MAX_VALUE, random);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // TODO Keep catalyzed state
        // if (this.canReactToWater(pos, state) && world instanceof World realWorld) {
        //     BlockPos waterNeighborPos = LivingVerdantVineBlock.getWaterNeighborPos(world, pos);
        //     if (waterNeighborPos != null) {
        //         WaterReactionManager.catalyzeAroundWater(realWorld, waterNeighborPos);
        //         state = state.with(ModProperties.CATALYZED, true);
        //     }
        // }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_CRIMSON_SPIKE;
    }

    @Override
    public boolean canReactToWater(BlockPos pos, BlockState state) {
        return !state.get(ModProperties.CATALYZED);
    }

    @Override
    public boolean reactToWater(World world, BlockPos pos, BlockState state, int waterAmount,
            Random random) {
        return false;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return super.hasRandomTicks(state) || !state.get(ModProperties.CATALYZED);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ModProperties.CATALYZED);
    }
}
