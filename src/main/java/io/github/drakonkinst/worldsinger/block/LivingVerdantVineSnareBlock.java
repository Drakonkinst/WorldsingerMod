package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import io.github.drakonkinst.worldsinger.world.lumar.SporeGrowthSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class LivingVerdantVineSnareBlock extends VerdantVineSnareBlock implements
        SporeKillable, WaterReactiveBlock {

    public static final int RECATALYZE_VALUE = 25;

    public LivingVerdantVineSnareBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(ModProperties.CATALYZED, false));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(ModProperties.CATALYZED);
        super.appendProperties(builder);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState placementState = super.getPlacementState(ctx);
        if (placementState != null) {
            placementState = placementState.with(ModProperties.CATALYZED, true);
        }
        return placementState;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (!state.get(ModProperties.CATALYZED) && world.hasRain(pos.up())) {
            this.reactToWater(world, pos, state, Integer.MAX_VALUE, random);
        }
    }

    @Override
    public void reactToWater(ServerWorld world, BlockPos pos, BlockState state, int waterAmount,
            Random random) {
        world.setBlockState(pos, state.with(ModProperties.CATALYZED, true));
        Direction direction = VerdantVineSnareBlock.getDirection(state);
        Int3 dir = new Int3(direction.getOffsetX(), direction.getOffsetY(),
                direction.getOffsetZ());
        SporeGrowthSpawner.spawnVerdantSporeGrowth(world, pos.toCenterPos(), RECATALYZE_VALUE,
                waterAmount, false, true, dir);
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_VERDANT_VINE_SNARE;
    }
}
