package io.github.drakonkinst.examplemod.mixin;

import net.minecraft.block.WallBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WallBlock.class)
public abstract class WallBlockMixin {
    // @Redirect(method = "getShapeMap", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"))
    // private <S, V> ImmutableMap.Builder<BlockState, VoxelShape> injectCustomFluidShapes(ImmutableMap.Builder<BlockState, VoxelShape> instance, S key, V value) {
    //     // Do nothing for now
    //
    // }
}
