package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeGrowthSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class LivingAetherSporeFluidBlock extends AetherSporeFluidBlock implements SporeKillable,
        WaterReactiveBlock {

    public static final int CATALYZE_VALUE = 250;

    public LivingAetherSporeFluidBlock(FlowableFluid fluid, AetherSporeType aetherSporeType,
            Settings settings) {
        super(fluid, aetherSporeType, settings);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (world.hasRain(pos.up())) {
            this.reactToWater(world, pos, state, Integer.MAX_VALUE, random);
        }
    }

    @Override
    public void reactToWater(World world, BlockPos pos, BlockState state, int waterAmount,
            Random random) {

        if (aetherSporeType == AetherSporeType.VERDANT) {
            SporeGrowthSpawner.spawnVerdantSporeGrowth(world, pos.toCenterPos(), CATALYZE_VALUE,
                    waterAmount, true, false);
        }
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_SPORE_SEA_BLOCK;
    }
}
