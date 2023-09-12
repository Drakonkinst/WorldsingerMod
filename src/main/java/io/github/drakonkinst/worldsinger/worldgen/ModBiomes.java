package io.github.drakonkinst.worldsinger.worldgen;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;

public class ModBiomes {

    public static final RegistryKey<Biome> EMERALD_SEA = ModBiomes.of("emerald_sea");

    private static RegistryKey<Biome> of(String id) {
        return RegistryKey.of(RegistryKeys.BIOME, Worldsinger.id(id));
    }
}
