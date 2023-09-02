package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingVerdantVineBlock extends VerdantVineBlock implements
        SporeKillable {

    public LivingVerdantVineBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_VERDANT_VINE_BLOCK;
    }
}
