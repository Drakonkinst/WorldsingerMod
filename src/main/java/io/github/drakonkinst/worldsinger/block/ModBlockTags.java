package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModBlockTags {

    public static final TagKey<Block> AETHER_SPORE_SEA_BLOCKS = ModBlockTags.of(
            "aether_spore_sea_blocks");
    public static final TagKey<Block> AETHER_SPORE_BLOCKS = ModBlockTags.of("aether_spore_blocks");
    public static final TagKey<Block> VERDANT_VINE_BLOCK = ModBlockTags.of("verdant_vine_block");
    public static final TagKey<Block> VERDANT_VINE_BRANCH = ModBlockTags.of("verdant_vine_branch");
    public static final TagKey<Block> VERDANT_VINE_SNARE = ModBlockTags.of("verdant_vine_snare");
    public static final TagKey<Block> TWISTING_VERDANT_VINES = ModBlockTags.of(
            "twisting_verdant_vines");
    public static final TagKey<Block> VERDANT_VINES = ModBlockTags.of("verdant_vines");
    public static final TagKey<Block> KILLS_SPORES = ModBlockTags.of("kills_spores");
    public static final TagKey<Block> SPORES_CAN_GROW = ModBlockTags.of("spores_can_grow");
    public static final TagKey<Block> SPORES_CAN_BREAK = ModBlockTags.of("spores_can_break");

    public static final TagKey<Block> FLUIDS_CANNOT_BREAK = ModBlockTags.ofCommon(
            "fluids_cannot_break");

    private ModBlockTags() {}

    private static TagKey<Block> of(String id) {
        return TagKey.of(RegistryKeys.BLOCK, new Identifier(ModConstants.MOD_ID, id));
    }

    private static TagKey<Block> ofCommon(String id) {
        return TagKey.of(RegistryKeys.BLOCK, new Identifier(ModConstants.COMMON_ID, id));
    }
}
