package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingVerdantVineSnareBlock extends VerdantVineSnareBlock implements
        SporeKillable {

    public LivingVerdantVineSnareBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_VERDANT_VINE_SNARE;
    }
}
