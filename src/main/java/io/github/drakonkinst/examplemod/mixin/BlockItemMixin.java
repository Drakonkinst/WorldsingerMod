package io.github.drakonkinst.examplemod.mixin;

import io.github.drakonkinst.examplemod.Fluidlogged;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Shadow
    public abstract Block getBlock();

    // When placing into a fluid block, allow waterlogged
    @Inject(method = "getPlacementState", at = @At(value = "RETURN"), cancellable = true)
    private void injectCustomFluidPlacementState(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState placementState = getBlock().getPlacementState(context);
        if (placementState == null || placementState.contains(Fluidlogged.PROPERTY_FLUID)) {
            return;
        }
        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        int index = Fluidlogged.getFluidIndex(fluidState.getFluid());
        if (index > -1) {
            cir.setReturnValue(placementState.with(Fluidlogged.PROPERTY_FLUID, index));
        }
    }
}
