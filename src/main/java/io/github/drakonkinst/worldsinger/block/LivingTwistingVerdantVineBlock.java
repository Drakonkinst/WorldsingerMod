package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingTwistingVerdantVineBlock extends TwistingVerdantVineBlock implements
        SporeKillable {

    public LivingTwistingVerdantVineBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected Block getStem() {
        return ModBlocks.TWISTING_VERDANT_VINES_PLANT;
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_TWISTING_VERDANT_VINES;
    }
}
