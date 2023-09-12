package io.github.drakonkinst.worldsinger.worldgen.feature;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public final class ModFeatures {

    public static final Feature<ReplaceWithSporeSeaFeatureConfig> REPLACE_WITH_SPORE_SEA = register(
            "replace_with_spore_sea", new ReplaceWithSporeSeaFeature());

    private static <T extends FeatureConfig> Feature<T> register(String id, Feature<T> feature) {
        return Registry.register(Registries.FEATURE, new Identifier(ModConstants.MOD_ID, id),
                feature);
    }

    public static void initialize() {
    }

    private ModFeatures() {}
}