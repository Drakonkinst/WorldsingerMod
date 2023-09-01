package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModEntityTypeTags {

    public static TagKey<EntityType<?>> SPORES_ALWAYS_AFFECT = ModEntityTypeTags.of(
            "spores_always_affect");
    public static TagKey<EntityType<?>> SPORES_NEVER_AFFECT = ModEntityTypeTags.of(
            "spores_never_affect");

    private ModEntityTypeTags() {}

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(ModConstants.MOD_ID, id));
    }
}
