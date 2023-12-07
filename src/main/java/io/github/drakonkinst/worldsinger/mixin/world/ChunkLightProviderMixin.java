package io.github.drakonkinst.worldsinger.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkLightProvider.class)
public abstract class ChunkLightProviderMixin {

    @ModifyExpressionValue(method = "isTrivialForLighting", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOpaque()Z"))
    private static boolean fixTintedPartialBlockLighting(boolean original, BlockState blockState) {
        return original || blockState.isIn(ModBlockTags.OPAQUE_FOR_LIGHTING);
    }
}
