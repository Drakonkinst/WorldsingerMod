package io.github.drakonkinst.worldsinger.mixin.event;

import io.github.drakonkinst.worldsinger.event.ServerPlayerHurtCallback;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion;
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion.Conditions;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityHurtPlayerCriterion.class)
public abstract class EntityHurtPlayerCriterionServerPlayerHurtMixin extends
        AbstractCriterion<Conditions> {

    @Inject(method = "trigger", at = @At("TAIL"))
    private void firePlayerHurtEvent(ServerPlayerEntity player, DamageSource source, float dealt,
            float taken, boolean blocked, CallbackInfo ci) {
        ServerPlayerHurtCallback.EVENT.invoker().onHurt(player, source, dealt, taken, blocked);
    }
}
