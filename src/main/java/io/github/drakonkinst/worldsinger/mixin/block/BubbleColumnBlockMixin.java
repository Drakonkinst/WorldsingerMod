package io.github.drakonkinst.worldsinger.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BubbleColumnBlock.class)
public abstract class BubbleColumnBlockMixin {

    @WrapOperation(method = "getBubbleState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private static boolean addAlternateMagmaBlock1(BlockState instance, Block block,
            Operation<Boolean> original) {
        if (original.call(instance, block)) {
            return true;
        }
        return block.equals(Blocks.MAGMA_BLOCK) && instance.isOf(ModBlocks.MAGMA_VENT);
    }

    @WrapOperation(method = "canPlaceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
    private boolean addAlternateMagmaBlock2(BlockState instance, Block block,
            Operation<Boolean> original) {
        if (original.call(instance, block)) {
            return true;
        }
        return block.equals(Blocks.MAGMA_BLOCK) && instance.isOf(ModBlocks.MAGMA_VENT);
    }
}
