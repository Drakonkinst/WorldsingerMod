package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingVerdantVineBranchBlock extends VerdantVineBranchBlock implements
        SporeKillable {

    private final Block deadSporeBlock;

    public LivingVerdantVineBranchBlock(Block deadSporeBlock, Settings settings) {
        super(settings);
        this.deadSporeBlock = deadSporeBlock;
    }

    @Override
    public Block getDeadSporeBlock() {
        return deadSporeBlock;
    }
}
