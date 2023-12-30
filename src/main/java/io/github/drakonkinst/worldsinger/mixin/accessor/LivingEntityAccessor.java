package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Invoker("tickActiveItemStack")
    void worldsinger$tickActiveItemStack();

    @Invoker("setLivingFlag")
    void worldsinger$setLivingFlag(int mask, boolean value);

    @Accessor("jumping")
    boolean worldsinger$isJumping();

    @Invoker("applyDamage")
    void worldsinger$applyDamage(DamageSource source, float amount);
}
