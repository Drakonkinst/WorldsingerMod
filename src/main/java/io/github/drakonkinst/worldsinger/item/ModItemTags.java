package io.github.drakonkinst.worldsinger.item;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModItemTags {

    public static final TagKey<Item> FLINT_AND_STEEL_VARIANTS = ModItemTags.of(
            "flint_and_steel_variants");
    public static final TagKey<Item> HAS_STEEL = ModItemTags.of("has_steel");
    public static final TagKey<Item> HAS_IRON = ModItemTags.of("has_iron");

    public static final TagKey<Item> SILVER_INGOTS = ModItemTags.ofCommon("silver_ingots");
    public static final TagKey<Item> SHIELDS = ModItemTags.ofCommon("shields");

    private static TagKey<Item> of(String id) {
        return TagKey.of(RegistryKeys.ITEM, Worldsinger.id(id));
    }

    private static TagKey<Item> ofCommon(String id) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(ModConstants.COMMON_ID, id));
    }

    private ModItemTags() {}
}
