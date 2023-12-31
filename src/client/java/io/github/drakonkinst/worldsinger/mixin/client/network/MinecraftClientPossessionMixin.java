package io.github.drakonkinst.worldsinger.mixin.client.network;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.drakonkinst.worldsinger.entity.CameraPossessable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientPossessionMixin {

    @Shadow
    @Nullable
    public abstract Entity getCameraEntity();

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    private void preventAttackIfPossessing(CallbackInfoReturnable<Boolean> cir) {
        if (getCameraEntity() instanceof CameraPossessable cameraPossessable
                && !cameraPossessable.canAttack()) {
            cir.setReturnValue(false);
        }
    }

    @ModifyExpressionValue(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"))
    private Type preventBlockBreakingIfPossessing(Type original) {
        if (getCameraEntity() instanceof CameraPossessable cameraPossessable
                && !cameraPossessable.canBreakBlock()) {
            return Type.MISS;
        }
        return original;
    }

    @ModifyExpressionValue(method = "doItemPick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"))
    private Type preventBlockPickIfPossessing(Type original) {
        if (getCameraEntity() instanceof CameraPossessable cameraPossessable
                && !cameraPossessable.canPickBlock()) {
            return Type.MISS;
        }
        return original;
    }
}
