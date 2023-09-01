package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.block.SporeKillable;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeethe;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class LivingAetherSporeFluid extends AetherSporeFluid {

    private static final int NUM_RANDOM_SPREAD_PER_RANDOM_TICK = 2;

    public LivingAetherSporeFluid(AetherSporeType aetherSporeType) {
        super(aetherSporeType);
    }

    @Override
    protected boolean hasRandomTicks() {
        return true;
    }

    @Override
    protected void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
        if (!LumarSeethe.areSporesFluidized(world)) {
            return;
        }

        // Spread to nearby dead spore sea blocks, regenerating them
        BlockState blockState = this.toBlockState(state);
        for (int i = 0; i < NUM_RANDOM_SPREAD_PER_RANDOM_TICK; ++i) {
            int offsetX = random.nextInt(3) - 1;
            int offsetY = random.nextInt(3) - 1;
            int offsetZ = random.nextInt(3) - 1;

            BlockPos blockPos = pos.add(offsetX, offsetY, offsetZ);
            if (world.getBlockState(blockPos).isOf(ModBlocks.DEAD_SPORE_SEA_BLOCK)
                    && world.getFluidState(blockPos).isStill()
                    && !SporeKillable.isSporeKillingBlockNearby(world, blockPos)) {
                world.setBlockState(blockPos, blockState);
            }
        }
        super.onRandomTick(world, pos, state, random);
    }
}
