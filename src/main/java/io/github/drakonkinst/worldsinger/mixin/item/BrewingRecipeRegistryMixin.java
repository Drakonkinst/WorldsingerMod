package io.github.drakonkinst.worldsinger.mixin.item;

import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public abstract class BrewingRecipeRegistryMixin {

    @Inject(method = "registerDefaults", at = @At("TAIL"))
    private static void addModBrewingRecipes(CallbackInfo ci) {
        // Causes the game to crash for some reason
        // BrewingRecipeRegistry.registerPotionType(ModItems.VERDANT_SPORES_BOTTLE);
        // BrewingRecipeRegistry.registerPotionType(ModItems.VERDANT_SPORES_SPLASH_BOTTLE);
        // BrewingRecipeRegistry.registerItemRecipe(ModItems.VERDANT_SPORES_BOTTLE, Items.GUNPOWDER,
        //         ModItems.VERDANT_SPORES_SPLASH_BOTTLE);
    }
}
