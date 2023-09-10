package io.github.drakonkinst.worldsinger.mixin.block;

import io.github.drakonkinst.worldsinger.mixin.accessor.BrewingRecipeRegistryAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.BrewingStandScreenHandler$PotionSlot")
public abstract class BrewingStandScreenHandlerPotionSlotMixin {

    @Inject(method = "matches", at = @At("HEAD"), cancellable = true)
    private static void allowCustomPotions(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        for (Ingredient ingredient : BrewingRecipeRegistryAccessor.getPotionTypes()) {
            if (ingredient.test(stack)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
