package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.block.WaterReactiveBlock;
import io.github.drakonkinst.worldsinger.world.WaterReactive;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface WaterReactiveFluid extends WaterReactive {

    boolean reactToWater(World world, BlockPos pos, FluidState fluidState, int waterAmount,
            Random random);

    default boolean reactToWater(World world, BlockPos pos, FluidState fluidState,
            int waterAmount) {
        return reactToWater(world, pos, fluidState, waterAmount, WaterReactiveBlock.random);
    }

    default boolean reactToWater(World world, BlockPos pos, int waterAmount) {
        return reactToWater(world, pos, world.getFluidState(pos), waterAmount,
                WaterReactiveBlock.random);
    }
}
