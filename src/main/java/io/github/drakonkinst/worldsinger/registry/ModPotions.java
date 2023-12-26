package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.BrewingRecipeRegistry;

public final class ModPotions {

    public static void initialize() {
        BrewingRecipeRegistry.registerPotionType(ModItems.DEAD_SPORES_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.DEAD_SPORES_SPLASH_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.VERDANT_SPORES_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.VERDANT_SPORES_SPLASH_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.CRIMSON_SPORES_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.CRIMSON_SPORES_SPLASH_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.ZEPHYR_SPORES_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.ZEPHYR_SPORES_SPLASH_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.SUNLIGHT_SPORES_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.SUNLIGHT_SPORES_SPLASH_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.ROSEITE_SPORES_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.ROSEITE_SPORES_SPLASH_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.MIDNIGHT_SPORES_BOTTLE);
        BrewingRecipeRegistry.registerPotionType(ModItems.MIDNIGHT_SPORES_SPLASH_BOTTLE);

        BrewingRecipeRegistry.registerItemRecipe(Items.POTION, ModItems.ZEPHYR_SPORES_BOTTLE,
                Items.SPLASH_POTION);

        ModPotions.registerCustomSplashPotion(ModItems.DEAD_SPORES_BOTTLE,
                ModItems.DEAD_SPORES_SPLASH_BOTTLE);
        ModPotions.registerCustomSplashPotion(ModItems.VERDANT_SPORES_BOTTLE,
                ModItems.VERDANT_SPORES_SPLASH_BOTTLE);
        ModPotions.registerCustomSplashPotion(ModItems.CRIMSON_SPORES_BOTTLE,
                ModItems.CRIMSON_SPORES_SPLASH_BOTTLE);
        ModPotions.registerCustomSplashPotion(ModItems.ZEPHYR_SPORES_BOTTLE,
                ModItems.ZEPHYR_SPORES_SPLASH_BOTTLE);
        ModPotions.registerCustomSplashPotion(ModItems.SUNLIGHT_SPORES_BOTTLE,
                ModItems.SUNLIGHT_SPORES_SPLASH_BOTTLE);
        ModPotions.registerCustomSplashPotion(ModItems.ROSEITE_SPORES_BOTTLE,
                ModItems.ROSEITE_SPORES_SPLASH_BOTTLE);
        ModPotions.registerCustomSplashPotion(ModItems.MIDNIGHT_SPORES_BOTTLE,
                ModItems.MIDNIGHT_SPORES_SPLASH_BOTTLE);

    }

    private static void registerCustomSplashPotion(Item regularPotion, Item splashPotion) {
        BrewingRecipeRegistry.registerItemRecipe(regularPotion, Items.GUNPOWDER, splashPotion);
        BrewingRecipeRegistry.registerItemRecipe(regularPotion, ModItems.ZEPHYR_SPORES_BOTTLE,
                splashPotion);
    }

    private ModPotions() {}

}
