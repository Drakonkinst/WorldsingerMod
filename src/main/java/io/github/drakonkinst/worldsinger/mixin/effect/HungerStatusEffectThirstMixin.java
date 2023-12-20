package io.github.drakonkinst.worldsinger.mixin.effect;

import net.minecraft.entity.effect.HungerStatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HungerStatusEffect.class)
public class HungerStatusEffectThirstMixin {

    // Workaround for the issue where the Hunger status effect also affects Thirst.
    // However, this might cause issues with any other mod if they expect the Hunger effect to also
    // effect anything they've added.
    // Since the original method is not used, no need for WrapOperation here.
    @Redirect(method = "applyUpdateEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExhaustion(F)V"))
    private void onlyDrainHunger(PlayerEntity instance, float exhaustion) {
        instance.getHungerManager().addExhaustion(exhaustion);
    }
}
