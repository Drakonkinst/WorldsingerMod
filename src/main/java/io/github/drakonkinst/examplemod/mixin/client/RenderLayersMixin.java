package io.github.drakonkinst.examplemod.mixin.client;

import io.github.drakonkinst.examplemod.fluid.ModFluidTags;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLayers.class)
public abstract class RenderLayersMixin {

    @Inject(method = "getFluidLayer", at = @At("RETURN"), cancellable = true)
    private static void makeLavaRenderTranslucent(FluidState state,
            CallbackInfoReturnable<RenderLayer> cir) {
        // if (state.isIn(FluidTags.LAVA)) {
        //     cir.setReturnValue(RenderLayer.getTranslucent());
        //     return;
        // }
        if (state.isIn(ModFluidTags.AETHER_SPORES)) {
            cir.setReturnValue(RenderLayer.getTranslucent());
        }
    }
}
