package io.github.drakonkinst.worldsinger.mixin.block;

import io.github.drakonkinst.worldsinger.mixin.accessor.BrewingRecipeRegistryAccessor;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public abstract class BrewingStandBlockEntityMixin {

    @Inject(method = "isValid", at = @At("RETURN"), cancellable = true)
    private void allowCustomPotions(int slot, ItemStack stack,
            CallbackInfoReturnable<Boolean> cir) {
        if (slot == 3 || slot == 4 || !this.getStack(slot).isEmpty()) {
            return;
        }
        for (Ingredient ingredient : BrewingRecipeRegistryAccessor.getPotionTypes()) {
            if (ingredient.test(stack)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Shadow
    public abstract ItemStack getStack(int slot);
}
