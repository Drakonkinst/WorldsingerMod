package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SporeKillable {

    Block getDeadSporeBlock();

    default void killSporeBlock(World world, BlockPos pos, BlockState state) {
        BlockState newBlockState = this.getDeadSporeBlock()
                .getStateWithProperties(state);
        world.setBlockState(pos, newBlockState);
    }
}
