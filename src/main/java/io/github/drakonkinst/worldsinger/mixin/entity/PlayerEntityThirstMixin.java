package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.component.ModComponents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityThirstMixin extends LivingEntity {

    protected PlayerEntityThirstMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    // Note: Because of this, thirst also decreases faster when under the Hunger effect.
    // If we don't want this, the option here would be to mixin into HungerStatusEffect and
    // PlayerEntity so only hunger is affected by this status effect.
    // Only worth doing this if Thirst should decrease the same way as hunger; should look into
    // other alternatives.
    @Inject(method = "addExhaustion", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;addExhaustion(F)V"))
    private void addThirstExhaustion(float exhaustion, CallbackInfo ci) {
        ModComponents.THIRST_MANAGER.get(this).addExhaustion(exhaustion);
    }

    // private void addHungerExhaustionOnly()
}
