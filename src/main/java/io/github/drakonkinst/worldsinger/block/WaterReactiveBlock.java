package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public interface WaterReactiveBlock {

    Random random = Random.create();

    void reactToWater(ServerWorld world, BlockPos pos, BlockState state, int waterAmount,
            Random random);

    default void reactToWater(ServerWorld world, BlockPos pos, BlockState state, int waterAmount) {
        reactToWater(world, pos, state, waterAmount, random);
    }
}
