package io.github.drakonkinst.worldsinger.mixin.fluid;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin {

    @Inject(method = "canFill", at = @At("HEAD"), cancellable = true)
    private void makeCanFillDataDriven(BlockView world, BlockPos pos, BlockState state, Fluid fluid,
            CallbackInfoReturnable<Boolean> cir) {
        if (state.isIn(ModBlockTags.FLUIDS_CANNOT_BREAK)) {
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(method = "getVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;blocksMovement()Z"))
    private boolean sunlightBlocksMovement(BlockState instance, Operation<Boolean> original) {
        if (instance.isOf(ModBlocks.SUNLIGHT)) {
            return true;
        }
        return original.call(instance);
    }
}
