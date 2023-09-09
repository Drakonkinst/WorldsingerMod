package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.LumarSeethe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class VerdantVineBlock extends PillarBlock {

    // Vine blocks decay if not persistent, is not raining, and seethe is on.
    // They decay faster if open to sky or above a fluid.
    public static boolean canDecay(ServerWorld world, BlockPos pos, BlockState state,
            Random random) {
        return LumarSeethe.areSporesFluidized(world)
                && !state.get(Properties.PERSISTENT)
                && !world.isRaining()
                && (world.isSkyVisible(pos.up()) || !world.getFluidState(pos.down()).isOf(
                Fluids.EMPTY) || random.nextInt(5) == 0);
    }

    public VerdantVineBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(Properties.PERSISTENT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.PERSISTENT);
        super.appendProperties(builder);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState placementState = super.getPlacementState(ctx);
        if (placementState != null) {
            placementState = placementState.with(Properties.PERSISTENT, true);
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
        if (VerdantVineBlock.canDecay(world, pos, state, random)) {
            world.breakBlock(pos, true);
        }
    }
}
