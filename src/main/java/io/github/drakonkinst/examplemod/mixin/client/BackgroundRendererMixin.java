package io.github.drakonkinst.examplemod.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.drakonkinst.examplemod.Constants;
import io.github.drakonkinst.examplemod.fluid.AetherSporeFluid;
import io.github.drakonkinst.examplemod.fluid.ModFluidTags;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

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
            if (fluidState.getFluid() instanceof AetherSporeFluid aetherSporeFluid) {
                red = aetherSporeFluid.getFogRed();
                green = aetherSporeFluid.getFogGreen();
                blue = aetherSporeFluid.getFogBlue();
            } else {
                Constants.LOGGER.error(
                        "Expected fluid to be an instance of AetherSporeFluid since it is in the tag "
                                + ModFluidTags.AETHER_SPORES.id().toString());
            }
        }
    }

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
            RenderSystem.setShaderFogStart(fogData.fogStart);
            RenderSystem.setShaderFogEnd(fogData.fogEnd);
            RenderSystem.setShaderFogShape(fogData.fogShape);
            // Prevent the original fog data from being used by exiting early
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
        Chunk currentChunk = cameraWorld.getChunk(cameraBlockPos);
        FluidState fluidState = currentChunk.getFluidState(cameraBlockPos);

        BlockView area = cameraWorld.getChunkAsView(currentChunk.getPos().x,
                currentChunk.getPos().z);
        float fluidHeight = fluidState.getHeight(area, cameraBlockPos);
        double yPos = camera.getPos().getY();
        boolean submersedInFluid = yPos < cameraBlockPos.getY() + fluidHeight;

        if (submersedInFluid) {
            return fluidState;
        }
        return null;
    }
}
