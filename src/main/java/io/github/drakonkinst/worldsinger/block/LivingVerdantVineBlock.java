package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingVerdantVineBlock extends VerdantVineBlock implements
        SporeKillable {

    private final Block deadSporeBlock;

    public LivingVerdantVineBlock(Block deadSporeBlock, Settings settings) {
        super(settings);
        this.deadSporeBlock = deadSporeBlock;
    }

    @Override
    public Block getDeadSporeBlock() {
        return deadSporeBlock;
    }
}
