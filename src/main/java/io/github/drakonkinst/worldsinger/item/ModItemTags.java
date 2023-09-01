package io.github.drakonkinst.worldsinger.item;

import io.github.drakonkinst.worldsinger.util.Constants;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModItemTags {

    public static final TagKey<Item> SALT = ModItemTags.ofCommon("salt");
    public static final TagKey<Item> SILVER_INGOTS = ModItemTags.ofCommon("silver_ingots");

    private static TagKey<Item> of(String id) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(Constants.MOD_ID, id));
    }

    private static TagKey<Item> ofCommon(String id) {
        return TagKey.of(RegistryKeys.ITEM, new Identifier(Constants.COMMON_ID, id));
    }

    private ModItemTags() {}
}
