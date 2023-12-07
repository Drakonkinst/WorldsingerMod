package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.cosmere.WaterReactive;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface WaterReactiveFluid extends WaterReactive {

    boolean reactToWater(World world, BlockPos pos, FluidState fluidState, int waterAmount,
            Random random);

    default boolean reactToWater(World world, BlockPos pos, FluidState fluidState,
            int waterAmount) {
        return reactToWater(world, pos, fluidState, waterAmount, random);
    }

    default boolean reactToWater(World world, BlockPos pos, int waterAmount) {
        return reactToWater(world, pos, world.getFluidState(pos), waterAmount, random);
    }
}
