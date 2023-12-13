package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.api.ModApi;
import io.github.drakonkinst.worldsinger.cosmere.SilverLined;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;

public final class ModModelPredicates {

    public static void register() {
        ModModelPredicates.registerModelPredicate("silver_lined", ((stack, world, entity, seed) -> {
            SilverLined silverItemData = ModApi.SILVER_LINED_ITEM.find(stack, null);
            if (silverItemData == null) {
                return 0.0f;
            }
            return silverItemData.getSilverDurability();
        }));
    }

    // Note: Unlike vanilla model predicates, you must specify the mod ID namespace for these
    private static void registerModelPredicate(String id, ClampedModelPredicateProvider provider) {
        ModelPredicateProviderRegistry.register(Worldsinger.id(id), provider);
    }

    private ModModelPredicates() {}
}
