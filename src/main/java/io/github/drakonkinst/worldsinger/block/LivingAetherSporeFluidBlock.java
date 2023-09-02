package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.minecraft.block.Block;
import net.minecraft.fluid.FlowableFluid;

public class LivingAetherSporeFluidBlock extends AetherSporeFluidBlock implements SporeKillable {

    public LivingAetherSporeFluidBlock(FlowableFluid fluid, AetherSporeType aetherSporeType,
            Settings settings) {
        super(fluid, aetherSporeType, settings);
    }

    @Override
    public Block getDeadSporeBlock() {
        return ModBlocks.DEAD_SPORE_SEA_BLOCK;
    }
}
