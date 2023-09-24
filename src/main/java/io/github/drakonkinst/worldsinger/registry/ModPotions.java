package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.item.ModItems;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;

public final class ModPotions {

    public static void register() {
        BrewingRecipeRegistry.registerPotionType(ModItems.VERDANT_SPORES_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.VERDANT_SPORES_SPLASH_BOTTLE);
        BrewingRecipeRegistry.registerItemRecipe(ModItems.VERDANT_SPORES_BOTTLE, Items.GUNPOWDER,
                ModItems.VERDANT_SPORES_SPLASH_BOTTLE);
    }

    private ModPotions() {}

}
