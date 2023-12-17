package io.github.drakonkinst.worldsinger.mixin.client.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin {

    @Inject(method = "isSameFluid", at = @At("HEAD"), cancellable = true)
    private static void makeSporeFluidsIdentical(FluidState a, FluidState b,
            CallbackInfoReturnable<Boolean> cir) {
        if (a.isIn(ModFluidTags.AETHER_SPORES) && b.isIn(ModFluidTags.AETHER_SPORES)) {
            cir.setReturnValue(a.isStill() == b.isStill());
        }
    }

    @WrapOperation(method = "getFluidHeight(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/fluid/Fluid;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/Fluid;matchesType(Lnet/minecraft/fluid/Fluid;)Z"))
    private boolean makeSporeFluidsIdentical2(Fluid instance, Fluid fluid,
            Operation<Boolean> original) {
        if (instance.getDefaultState().isIn(ModFluidTags.AETHER_SPORES) && fluid.getDefaultState()
                .isIn(ModFluidTags.AETHER_SPORES)) {
            return true;
        }
        // Default behavior
        return original.call(instance, fluid);
    }

    @WrapOperation(method = "getFluidHeight(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/fluid/Fluid;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSolid()Z"))
    private boolean fixSunlightBlockRendering(BlockState instance, Operation<Boolean> original) {
        if (instance.isOf(ModBlocks.SUNLIGHT)) {
            return true;
        }
        return original.call(instance);
    }
}
