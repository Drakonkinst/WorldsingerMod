package io.github.drakonkinst.worldsinger.world;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

public interface Metal {

    TagKey<Block> getBlockTag();

    TagKey<Item> getItemTag();

    TagKey<EntityType<?>> getEntityTypeTag();
}
