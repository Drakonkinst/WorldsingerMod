package io.github.drakonkinst.worldsinger.worldgen.feature;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public final class ModConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> VERDANT_SPORE_SEA = ModConfiguredFeatures.of(
            "verdant_spore_sea");

    public static RegistryKey<ConfiguredFeature<?, ?>> of(String id) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,
                Worldsinger.id(id));
    }

    private ModConfiguredFeatures() {}
}
