package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingVerdantVineBranchBlock extends VerdantVineBranchBlock implements
        SporeKillable {

    public LivingVerdantVineBranchBlock(Settings settings) {
        super(settings);
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_VERDANT_VINE_BRANCH;
    }
}
