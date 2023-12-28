package io.github.drakonkinst.worldsinger.mixin.client.gui;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.gui.ThirstStatusBar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(method = "renderStatusBars", at = @At("TAIL"))
    private void renderThirstStatusBar(DrawContext context, CallbackInfo ci) {
        PlayerEntity player = this.getCameraPlayer();
        if (player == null) {
            return;
        }
        if (ThirstStatusBar.shouldRenderThirstBar(player)) {
            this.client.getProfiler().push("thirst");
            ThirstStatusBar.renderThirstStatusBar(client, context, player);
            this.client.getProfiler().pop();
        }
    }

    // Currently, this method is only used to get the number of health rows the player's mount has,
    // so it knows where to render the air meter.
    // Add an extra row to give space for the thirst meter, if it should render.
    @ModifyReturnValue(method = "getHeartRows", at = @At("RETURN"))
    private int adjustAirStatusMeter(int original) {
        if (ThirstStatusBar.shouldRenderThirstBar(this.getCameraPlayer())) {
            return original + 1;
        }
        return original;
    }

    @Shadow
    @Nullable
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    @Final
    private MinecraftClient client;
}
