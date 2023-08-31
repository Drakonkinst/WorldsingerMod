package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingTwistingVerdantVineBlock extends TwistingVerdantVineBlock implements
        SporeKillable {

    private final Block deadSporeBlock;

    public LivingTwistingVerdantVineBlock(Block deadSporeBlock, Settings settings) {
        super(settings);
        this.deadSporeBlock = deadSporeBlock;
    }

    @Override
    protected Block getStem() {
        return ModBlocks.TWISTING_VERDANT_VINES_PLANT;
    }

    @Override
    public Block getDeadSporeBlock() {
        return deadSporeBlock;
    }
}
