package io.github.drakonkinst.worldsinger.cosmere;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface WaterReactive {

    Random random = Random.create();

    boolean reactToWater(World world, BlockPos pos, int waterAmount);
}
