package io.github.drakonkinst.worldsinger.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface WaterReactive {

    boolean reactToWater(World world, BlockPos pos, int waterAmount);
}
