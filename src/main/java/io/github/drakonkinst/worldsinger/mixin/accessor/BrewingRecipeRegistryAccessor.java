package io.github.drakonkinst.worldsinger.mixin.accessor;

import java.util.List;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingRecipeRegistry.class)
public interface BrewingRecipeRegistryAccessor {

    @Accessor("POTION_TYPES")
    static List<Ingredient> getPotionTypes() {
        throw new UnsupportedOperationException();
    }
}
