package io.github.drakonkinst.worldsinger.mixin.client;

import net.minecraft.client.render.RenderLayers;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderLayers.class)
public abstract class RenderLayersMixin {

    // @Inject(method = "getFluidLayer", at = @At("RETURN"), cancellable = true)
    // private static void makeLavaRenderTranslucent(FluidState state,
    //         CallbackInfoReturnable<RenderLayer> cir) {
    //     if (state.isIn(FluidTags.LAVA)) {
    //         cir.setReturnValue(RenderLayer.getTranslucent());
    //     }
    // }
}
