package io.github.drakonkinst.worldsinger.mixin.fluid;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WaterFluid.class)
public abstract class WaterFluidMixin {

    // Spore fluid should not be able to override water
    @ModifyReturnValue(method = "canBeReplacedWith", at = @At("RETURN"))
    private boolean checkSporeFluid(boolean canBeReplaced, FluidState state, BlockView world,
            BlockPos pos, Fluid fluid, Direction direction) {
        return canBeReplaced && !fluid.getDefaultState().isIn(ModFluidTags.AETHER_SPORES);
    }
}
