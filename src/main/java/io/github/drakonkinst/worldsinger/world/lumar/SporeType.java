package io.github.drakonkinst.worldsinger.world.lumar;

import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.util.StringIdentifiable;

// This isn't really usable without AetherSporeType since it interferes with Codecs, but figured
// I'd leave it here, so it can be used where it can.
public interface SporeType extends StringIdentifiable {

    int getColor();

    int getParticleColor();

    StatusEffect getStatusEffect();

    Item getBottledItem();

    Item getBucketItem();

    FlowableFluid getFluid();

    Block getFluidBlock();

    Block getSolidBlock();
}
