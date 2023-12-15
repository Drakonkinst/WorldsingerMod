package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModEntityTypeTags {

    public static final TagKey<EntityType<?>> SPORES_ALWAYS_AFFECT = ModEntityTypeTags.ofCommon(
            "spores_always_affect");
    public static final TagKey<EntityType<?>> SPORES_NEVER_AFFECT = ModEntityTypeTags.ofCommon(
            "spores_never_affect");
    public static TagKey<EntityType<?>> HAS_STEEL = ModEntityTypeTags.ofCommon("has_steel");
    public static TagKey<EntityType<?>> HAS_IRON = ModEntityTypeTags.ofCommon("has_iron");
    public static TagKey<EntityType<?>> MIDNIGHT_CREATURES_CANNOT_IMITATE = ModEntityTypeTags.ofCommon(
            "midnight_creatures_cannot_imitate");

    private static TagKey<EntityType<?>> of(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, Worldsinger.id(id));
    }

    private static TagKey<EntityType<?>> ofCommon(String id) {
        return TagKey.of(RegistryKeys.ENTITY_TYPE, new Identifier(ModConstants.COMMON_ID, id));
    }

    private ModEntityTypeTags() {}
}
