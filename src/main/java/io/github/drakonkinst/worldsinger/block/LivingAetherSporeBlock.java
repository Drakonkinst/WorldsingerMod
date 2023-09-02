package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.minecraft.block.Block;

public class LivingAetherSporeBlock extends AetherSporeBlock implements SporeKillable {

    public LivingAetherSporeBlock(AetherSporeType aetherSporeType, Block fluidized,
            Settings settings) {
        super(aetherSporeType, fluidized, settings);
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_SPORE_BLOCK;
    }
}
