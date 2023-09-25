package io.github.drakonkinst.worldsinger.worldgen;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.worldgen.lumar.LumarWaterLakeFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;

public final class ModFeatures {

    public static final Feature<DefaultFeatureConfig> LUMAR_WATER_LAKE = register(
            "lumar_water_lake", new LumarWaterLakeFeature(DefaultFeatureConfig.CODEC));

    private static <T extends FeatureConfig> Feature<T> register(String id, Feature<T> feature) {
        return Registry.register(Registries.FEATURE, Worldsinger.id(id),
                feature);
    }

    public static void initialize() {
    }

    private ModFeatures() {}
}
