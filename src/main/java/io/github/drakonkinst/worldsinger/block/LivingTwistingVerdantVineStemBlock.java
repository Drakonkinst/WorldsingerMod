package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingTwistingVerdantVineStemBlock extends TwistingVerdantVineStemBlock implements
        SporeKillable {

    private final Block deadSporeBlock;

    public LivingTwistingVerdantVineStemBlock(Block deadSporeBlock, Settings settings) {
        super(settings);
        this.deadSporeBlock = deadSporeBlock;
    }

    @Override
    protected Block getBud() {
        return ModBlocks.TWISTING_VERDANT_VINES;
    }

    @Override
    public Block getDeadSporeBlock() {
        return deadSporeBlock;
    }
}
