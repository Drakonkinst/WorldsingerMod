package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingVerdantVineSnareBlock extends VerdantVineSnareBlock implements
        SporeKillable {

    private final Block deadSporeBlock;

    public LivingVerdantVineSnareBlock(Block deadSporeBlock, Settings settings) {
        super(settings);
        this.deadSporeBlock = deadSporeBlock;
    }

    @Override
    public Block getDeadSporeBlock() {
        return deadSporeBlock;
    }
}
