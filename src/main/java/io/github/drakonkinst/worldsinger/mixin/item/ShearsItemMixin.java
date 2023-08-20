package io.github.drakonkinst.worldsinger.mixin.item;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {

    @Unique
    private static final float EFFICIENT_MULTIPLIER = 5.0f;

    @Inject(method = "getMiningSpeedMultiplier", at = @At("RETURN"), cancellable = true)
    private void addEfficientBlockTag(ItemStack stack, BlockState state,
            CallbackInfoReturnable<Float> cir) {
        if (state.isIn(ModBlockTags.SHEAR_MINEABLE)) {
            cir.setReturnValue(EFFICIENT_MULTIPLIER);
        }
    }
}
