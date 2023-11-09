package io.github.drakonkinst.worldsinger.world.lumar;

import net.minecraft.block.Block;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.util.StringIdentifiable;

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
