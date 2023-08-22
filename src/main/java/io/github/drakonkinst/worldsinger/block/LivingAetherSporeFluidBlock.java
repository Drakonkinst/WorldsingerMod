package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.Block;
import net.minecraft.fluid.FlowableFluid;

public class LivingAetherSporeFluidBlock extends AetherSporeFluidBlock implements SporeKillable {

    private final Block deadSporeBlock;

    public LivingAetherSporeFluidBlock(FlowableFluid fluid, Block deadSporeBlock,
            Settings settings) {
        super(fluid, settings);
        this.deadSporeBlock = deadSporeBlock;
    }

    @Override
    public Block getDeadSporeBlock() {
        return deadSporeBlock;
    }
}
