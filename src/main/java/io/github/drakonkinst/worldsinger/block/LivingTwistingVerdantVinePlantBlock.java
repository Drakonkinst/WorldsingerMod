package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingTwistingVerdantVinePlantBlock extends TwistingVerdantVinePlantBlock implements
        SporeKillable {

    private final Block deadSporeBlock;

    public LivingTwistingVerdantVinePlantBlock(Block deadSporeBlock, Settings settings) {
        super(settings);
        this.deadSporeBlock = deadSporeBlock;
    }

    @Override
    protected Block getStem() {
        return ModBlocks.TWISTING_VERDANT_VINES;
    }

    @Override
    public Block getDeadSporeBlock() {
        return deadSporeBlock;
    }
}
