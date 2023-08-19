package io.github.drakonkinst.worldsinger.mixin.fluid;

import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WaterFluid.class)
public abstract class WaterFluidMixin {

    // Spore fluid should not be able to override water
    @Inject(method = "canBeReplacedWith", at = @At("RETURN"), cancellable = true)
    private void checkSporeFluid(FluidState state, BlockView world, BlockPos pos, Fluid fluid,
            Direction direction, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(
                !fluid.getDefaultState().isIn(ModFluidTags.AETHER_SPORES) && cir.getReturnValue());
    }

}
