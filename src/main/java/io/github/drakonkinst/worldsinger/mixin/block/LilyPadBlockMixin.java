package io.github.drakonkinst.worldsinger.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LilyPadBlock.class)
public abstract class LilyPadBlockMixin {

    @ModifyReturnValue(method = "canPlantOnTop", at = @At("RETURN"))
    private boolean allowPlantingOnSporeFluid(boolean original, BlockState floor, BlockView world,
            BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos);
        return original || fluidState.isIn(ModFluidTags.AETHER_SPORES);
    }
}
