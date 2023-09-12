package io.github.drakonkinst.worldsinger.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.fluid.AetherSporeFluid;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogData;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {

    @Shadow
    private static float red;
    @Shadow
    private static float green;
    @Shadow
    private static float blue;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BackgroundRenderer;getFogModifier(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier;"))
    private static void injectCustomFluidColors(Camera camera, float tickDelta, ClientWorld world,
            int viewDistance, float skyDarkness, CallbackInfo ci) {
        FluidState fluidState = getSubmersedFluidState(camera);
        if (fluidState == null) {
            return;
        }

        if (fluidState.isIn(ModFluidTags.AETHER_SPORES)) {
            // This is equivalent to the much easier camera.getFocusedEntity().isSubmergedIn(ModFluidTags.AETHER_SPORES)
            // But we need to know the fluid state anyways
            if (fluidState.getFluid() instanceof AetherSporeFluid aetherSporeFluid) {
                red = aetherSporeFluid.getFogRed();
                green = aetherSporeFluid.getFogGreen();
                blue = aetherSporeFluid.getFogBlue();
            } else {
                Worldsinger.LOGGER.error(
                        "Expected fluid to be an instance of AetherSporeFluid since it is in the tag "
                                + ModFluidTags.AETHER_SPORES.id().toString());
            }
        }
    }

    // This doesn't work for some reason, maybe an issue with LocalRef?
    // @Inject(method = "applyFog", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/render/BackgroundRenderer$FogData;fogStart:F"))
    // private static void injectCustomFluidFogSettings(Camera camera, FogType fogType,
    //         float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci,
    //         @Local LocalRef<FogData> fogDataRef) {
    //     FluidState fluidState = getSubmersedFluidState(camera);
    //     if (fluidState == null) {
    //         return;
    //     }
    //
    //     Entity entity = camera.getFocusedEntity();
    //     if (fluidState.isIn(ModFluidTags.AETHER_SPORES)) {
    //         FogData fogData = new FogData(fogType);
    //         if (entity.isSpectator()) {
    //             // Match spectator mode settings for other fluids
    //             fogData.fogStart = -8.0f;
    //             fogData.fogEnd = viewDistance * 0.5f;
    //         } else {
    //             fogData.fogStart = AetherSporeFluid.FOG_START;
    //             fogData.fogEnd = AetherSporeFluid.FOG_END;
    //         }
    //         fogDataRef.set(fogData);
    //     }
    // }

    @Inject(method = "applyFog", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V"), cancellable = true)
    private static void injectCustomFluidFogSettings(Camera camera, FogType fogType,
            float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        FluidState fluidState = getSubmersedFluidState(camera);
        if (fluidState == null) {
            return;
        }

        Entity entity = camera.getFocusedEntity();
        if (fluidState.isIn(ModFluidTags.AETHER_SPORES)) {
            FogData fogData = new FogData(fogType);
            if (entity.isSpectator()) {
                // Match spectator mode settings for other fluids
                fogData.fogStart = -8.0f;
                fogData.fogEnd = viewDistance * 0.5f;
            } else {
                fogData.fogStart = AetherSporeFluid.FOG_START;
                fogData.fogEnd = AetherSporeFluid.FOG_END;
            }

            // Call the end of the method
            RenderSystem.setShaderFogStart(fogData.fogStart);
            RenderSystem.setShaderFogEnd(fogData.fogEnd);
            RenderSystem.setShaderFogShape(fogData.fogShape);
            ci.cancel();
        }
    }

    @Unique
    @Nullable
    private static FluidState getSubmersedFluidState(Camera camera) {
        if (!camera.isReady()) {
            return null;
        }

        World cameraWorld = camera.getFocusedEntity().getWorld();
        BlockPos cameraBlockPos = camera.getBlockPos();
        FluidState fluidState = cameraWorld.getFluidState(cameraBlockPos);

        float fluidHeight = fluidState.getHeight(cameraWorld, cameraBlockPos);
        double yPos = camera.getPos().getY();
        boolean submersedInFluid = yPos < cameraBlockPos.getY() + fluidHeight;

        if (submersedInFluid) {
            return fluidState;
        }
        return null;
    }
}
