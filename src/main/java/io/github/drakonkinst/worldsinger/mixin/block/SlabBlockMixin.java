package io.github.drakonkinst.worldsinger.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.ItemPlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SlabBlock.class)
public abstract class SlabBlockMixin {

    @ModifyReturnValue(method = "getPlacementState", at = @At("RETURN"))
    private BlockState injectRemoveFluidloggedStateIfDoubleSlab(BlockState placementState,
            ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf((Block) (Object) this)) {
            return placementState.with(ModProperties.FLUIDLOGGED, 0);
        }
        return placementState;
    }
}
