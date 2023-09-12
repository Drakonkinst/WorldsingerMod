package io.github.drakonkinst.worldsinger.worldgen;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class ModBiomes {

    public static final RegistryKey<Biome> EMERALD_SEA = ModBiomes.of("emerald_sea");

    private static RegistryKey<Biome> of(String id) {
        return RegistryKey.of(RegistryKeys.BIOME, new Identifier(ModConstants.MOD_ID, id));
    }
}
