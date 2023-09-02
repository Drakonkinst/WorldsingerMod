package io.github.drakonkinst.worldsinger.client;

import io.github.drakonkinst.worldsinger.api.ModApi;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.SilverLined;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public final class ModModelPredicates {

    public static void register() {
        registerModelPredicate("silver_lined", ((stack, world, entity, seed) -> {
            SilverLined silverItemData = ModApi.SILVER_LINED_ITEM.find(stack, null);
            if (silverItemData == null) {
                return 0.0f;
            }
            return silverItemData.getSilverDurability();
        }));
    }

    // Note: Unlike vanilla model predicates, you must specify the mod ID namespace for these
    private static void registerModelPredicate(String id, ClampedModelPredicateProvider provider) {
        ModelPredicateProviderRegistry.register(new Identifier(ModConstants.MOD_ID, id), provider);

    }

    private ModModelPredicates() {}
}
