package io.github.drakonkinst.worldsinger.mixin.client.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Inject(method = "interactEntity", at = @At("HEAD"), cancellable = true)
    private void preventInteractWithSelf(PlayerEntity player, Entity entity, Hand hand,
            CallbackInfoReturnable<ActionResult> cir) {
        if (entity.equals(MinecraftClient.getInstance().player)) {
            cir.cancel();
        }
    }

    @Inject(method = "interactEntityAtLocation", at = @At("HEAD"), cancellable = true)
    private void preventInteractAtLocationWithSelf(PlayerEntity player, Entity entity,
            EntityHitResult hitResult, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (entity.equals(MinecraftClient.getInstance().player)) {
            cir.cancel();
        }
    }

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void preventAttackingSelf(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (target.equals(MinecraftClient.getInstance().player)) {
            ci.cancel();
        }
    }
}
