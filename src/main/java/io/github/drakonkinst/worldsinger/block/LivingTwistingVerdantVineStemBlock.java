package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingTwistingVerdantVineStemBlock extends TwistingVerdantVineStemBlock implements
        SporeKillable {

    public LivingTwistingVerdantVineStemBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected Block getBud() {
        return ModBlocks.TWISTING_VERDANT_VINES;
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_TWISTING_VERDANT_VINES_PLANT;
    }
}
