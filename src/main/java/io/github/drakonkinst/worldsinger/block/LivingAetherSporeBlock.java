package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.minecraft.block.Block;

public class LivingAetherSporeBlock extends AetherSporeBlock implements SporeKillable {

    private final Block deadSporeBlock;

    public LivingAetherSporeBlock(AetherSporeType aetherSporeType, Block fluidized,
            Block deadSporeBlock,
            Settings settings) {
        super(aetherSporeType, fluidized, settings);
        this.deadSporeBlock = deadSporeBlock;
    }

    @Override
    public Block getDeadSporeBlock() {
        return deadSporeBlock;
    }
}
