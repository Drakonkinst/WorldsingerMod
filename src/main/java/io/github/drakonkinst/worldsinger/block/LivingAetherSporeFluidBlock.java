package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.minecraft.block.Block;
import net.minecraft.fluid.FlowableFluid;

public class LivingAetherSporeFluidBlock extends AetherSporeFluidBlock implements SporeKillable {

    private final Block deadSporeBlock;

    public LivingAetherSporeFluidBlock(FlowableFluid fluid, AetherSporeType aetherSporeType,
            Block deadSporeBlock, Settings settings) {
        super(fluid, aetherSporeType, settings);
        this.deadSporeBlock = deadSporeBlock;
    }

    @Override
    public Block getDeadSporeBlock() {
        return deadSporeBlock;
    }
}
