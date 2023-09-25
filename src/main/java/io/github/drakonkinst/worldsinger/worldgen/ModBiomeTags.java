package io.github.drakonkinst.worldsinger.worldgen;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public class ModBiomeTags {

    public static final TagKey<Biome> LUMAR_HAS_LAKES = ModBiomeTags.of("lumar_has_lakes");

    private static TagKey<Biome> of(String id) {
        return TagKey.of(RegistryKeys.BIOME, Worldsinger.id(id));
    }
}
