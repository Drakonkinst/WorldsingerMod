package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;

public class LivingAetherSporeBlock extends AetherSporeBlock implements SporeKillable {

    private final Block deadSporeBlock;

    public LivingAetherSporeBlock(Block fluidized, Block deadSporeBlock, int color,
            Settings settings) {
        super(fluidized, color, settings);
        this.deadSporeBlock = deadSporeBlock;
    }

    @Override
    public Block getDeadSporeBlock() {
        return deadSporeBlock;
    }
}
