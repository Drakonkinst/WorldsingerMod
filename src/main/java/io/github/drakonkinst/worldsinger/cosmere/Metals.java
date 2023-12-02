package io.github.drakonkinst.worldsinger.cosmere;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypeTags;
import io.github.drakonkinst.worldsinger.item.ModItemTags;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

public enum Metals implements Metal {
    IRON(ModBlockTags.HAS_IRON, ModItemTags.HAS_IRON, ModEntityTypeTags.HAS_IRON), STEEL(
            ModBlockTags.HAS_STEEL, ModItemTags.HAS_STEEL, ModEntityTypeTags.HAS_STEEL);

    private final TagKey<Block> blockTag;
    private final TagKey<Item> itemTag;
    private final TagKey<EntityType<?>> entityTypeTag;

    Metals(TagKey<Block> blockTag, TagKey<Item> itemTag, TagKey<EntityType<?>> entityTypeTag) {
        this.blockTag = blockTag;
        this.itemTag = itemTag;
        this.entityTypeTag = entityTypeTag;
    }

    @Override
    public TagKey<Block> getBlockTag() {
        return blockTag;
    }

    @Override
    public TagKey<Item> getItemTag() {
        return itemTag;
    }

    @Override
    public TagKey<EntityType<?>> getEntityTypeTag() {
        return entityTypeTag;
    }
}
