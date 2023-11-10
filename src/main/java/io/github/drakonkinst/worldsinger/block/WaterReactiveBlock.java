package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.WaterReactive;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface WaterReactiveBlock extends WaterReactive {

    boolean canReactToWater(BlockPos pos, BlockState state);

    boolean reactToWater(World world, BlockPos pos, BlockState state, int waterAmount,
            Random random);

    default boolean reactToWater(World world, BlockPos pos, BlockState state, int waterAmount) {
        return reactToWater(world, pos, state, waterAmount, random);
    }

    default boolean reactToWater(World world, BlockPos pos, int waterAmount) {
        return reactToWater(world, pos, world.getBlockState(pos), waterAmount, random);
    }
}
