package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeGrowthSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LivingAetherSporeBlock extends AetherSporeBlock implements SporeKillable,
        WaterReactiveBlock {

    public static final int CATALYZE_VALUE = 250;

    public LivingAetherSporeBlock(AetherSporeType aetherSporeType, Block fluidized,
            Settings settings) {
        super(aetherSporeType, fluidized, settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (world.hasRain(pos.up())) {
            this.reactToWater(world, pos, state, Integer.MAX_VALUE, random);
        }
    }

    @Override
    public boolean canReactToWater(BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean reactToWater(World world, BlockPos pos, BlockState state, int waterAmount,
            Random random) {
        world.removeBlock(pos, false);
        if (aetherSporeType == AetherSporeType.VERDANT) {
            SporeGrowthSpawner.spawnVerdantSporeGrowth(world, pos.toCenterPos(), CATALYZE_VALUE,
                    waterAmount, true, false, false);
        }
        return true;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
            BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (world instanceof World realWorld) {
            BlockPos waterNeighborPos = LivingVerdantVineBlock.getWaterNeighborPos(world, pos);
            if (waterNeighborPos != null) {
                SporeGrowthSpawner.catalyzeAroundWater(realWorld, waterNeighborPos);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos,
                neighborPos);
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_SPORE_BLOCK;
    }
}
