package io.github.drakonkinst.worldsinger.registry;

import net.minecraft.item.FoodComponent;

public final class ModFoodComponents {

    public static final FoodComponent SALT = new FoodComponent.Builder().hunger(1)
            .saturationModifier(0.1f)
            .snack()
            .build();

    private ModFoodComponents() {}
}
