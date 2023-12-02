package io.github.drakonkinst.worldsinger.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Shadow
    public abstract Block getBlock();

    // When placing into a fluid block, allow waterlogged
    @ModifyReturnValue(method = "getPlacementState", at = @At(value = "RETURN"))
    private BlockState injectCustomFluidPlacementState(BlockState original,
            ItemPlacementContext context) {
        // Do not add fluid if not fluidloggable
        if (original == null || !original.contains(ModProperties.FLUIDLOGGED)) {
            return original;
        }

        // Do not add fluid if double slab
        if (original.getBlock() instanceof SlabBlock
                && original.get(SlabBlock.TYPE) == SlabType.DOUBLE) {
            return original;
        }

        FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
        int index = Fluidlogged.getFluidIndex(fluidState.getFluid());
        // Do not add fluid if unrecognized fluid
        if (index < 0) {
            return original;
        }

        // Add fluid
        return original.with(ModProperties.FLUIDLOGGED, index);
    }
}
