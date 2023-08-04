package io.github.drakonkinst.examplemod.block;

import io.github.drakonkinst.examplemod.Constants;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModBlockTags {
    private ModBlockTags() {
    }

    public static final TagKey<Block> AETHER_SPORE_SEA_BLOCKS = ModBlockTags.of("aether_spore_sea_blocks");
    public static final TagKey<Block> AETHER_SPORE_BLOCKS = ModBlockTags.of("aether_spore_blocks");

    private static TagKey<Block> of(String id) {
        return TagKey.of(RegistryKeys.BLOCK, new Identifier(Constants.MOD_ID, id));
    }
}
