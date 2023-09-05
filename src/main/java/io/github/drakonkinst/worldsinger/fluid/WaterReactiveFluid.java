package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.block.WaterReactiveBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface WaterReactiveFluid {

    void reactToWater(World world, BlockPos pos, FluidState fluidState, int waterAmount,
            Random random);

    default void reactToWater(World world, BlockPos pos, FluidState fluidState,
            int waterAmount) {
        reactToWater(world, pos, fluidState, waterAmount, WaterReactiveBlock.random);
    }
}
