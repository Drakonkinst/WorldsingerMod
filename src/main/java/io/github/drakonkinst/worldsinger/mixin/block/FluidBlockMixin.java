package io.github.drakonkinst.worldsinger.mixin.block;

import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void registerFluidBlocks(FlowableFluid fluid, AbstractBlock.Settings settings,
            CallbackInfo ci) {
        Fluidlogged.registerFluidBlockForFluid(fluid, (FluidBlock) (Object) this);
    }
}
