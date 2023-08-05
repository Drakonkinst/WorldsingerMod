package io.github.drakonkinst.examplemod.mixin;

import io.github.drakonkinst.examplemod.fluid.ModFluidTags;
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
public class WaterFluidMixin {

    @Inject(method = "canBeReplacedWith", at = @At("RETURN"), cancellable = true)
    private void checkSporeFluid(FluidState state, BlockView world, BlockPos pos, Fluid fluid,
            Direction direction, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(!fluid.isIn(ModFluidTags.AETHER_SPORES) && cir.getReturnValue());
    }

}
