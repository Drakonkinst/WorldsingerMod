package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.util.ModProperties;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface LivingSporeGrowthBlock extends SporeKillable, WaterReactiveBlock {

    default boolean canReactToWater(BlockPos pos, BlockState state) {
        return !state.get(ModProperties.CATALYZED);
    }
}
