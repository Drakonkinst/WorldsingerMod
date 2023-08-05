package io.github.drakonkinst.examplemod.mixin;

import io.github.drakonkinst.examplemod.fluid.Fluidlogged;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlabBlock.class)
public abstract class SlabBlockMixin {
    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void injectRemoveFluidloggedStateIfDoubleSlab(ItemPlacementContext ctx,
                                                          CallbackInfoReturnable<BlockState> cir) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf((Block) (Object) this)) {
            cir.setReturnValue(cir.getReturnValue().with(Fluidlogged.PROPERTY_FLUID, 0));
        }
    }
}
