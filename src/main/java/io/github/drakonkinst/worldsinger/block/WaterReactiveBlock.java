package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface WaterReactiveBlock {

    Random random = Random.create();

    void reactToWater(World world, BlockPos pos, BlockState state, int waterAmount,
            Random random);

    default void reactToWater(World world, BlockPos pos, BlockState state, int waterAmount) {
        reactToWater(world, pos, state, waterAmount, random);
    }
}
