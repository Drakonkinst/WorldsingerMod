package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Invoker("tickActiveItemStack")
    void worldsinger$tickActiveItemStack();

    @Invoker("setLivingFlag")
    void worldsinger$setLivingFlag(int mask, boolean value);
}
