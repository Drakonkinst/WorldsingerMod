package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class ModEntityTypeTags {

    public static final TagKey<EntityType<?>> SPORES_ALWAYS_AFFECT = ModEntityTypeTags.of(
            "spores_always_affect");
    public static final TagKey<EntityType<?>> SPORES_NEVER_AFFECT = ModEntityTypeTags.of(
            "spores_never_affect");
    public static TagKey<EntityType<?>> HAS_STEEL = ModEntityTypeTags.of("has_steel");
    public static TagKey<EntityType<?>> HAS_IRON = ModEntityTypeTags.of("has_iron");

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, Worldsinger.id(id));
    }

    private ModEntityTypeTags() {}
}
