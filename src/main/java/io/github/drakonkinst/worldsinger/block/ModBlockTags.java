package io.github.drakonkinst.worldsinger.block;

import io.github.drakonkinst.worldsinger.util.Constants;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModBlockTags {

    private ModBlockTags() {}

    public static final TagKey<Block> AETHER_SPORE_SEA_BLOCKS = ModBlockTags.of(
            "aether_spore_sea_blocks");
    public static final TagKey<Block> AETHER_SPORE_BLOCKS = ModBlockTags.of("aether_spore_blocks");
    public static final TagKey<Block> VERDANT_VINE_BRANCH = ModBlockTags.of("verdant_vine_branch");
    public static final TagKey<Block> VERDANT_VINE_SNARE = ModBlockTags.of("verdant_vine_snare");
    public static final TagKey<Block> TWISTING_VERDANT_VINES = ModBlockTags.of(
            "twisting_verdant_vines");
    public static final TagKey<Block> KILLS_SPORES = ModBlockTags.of("kills_spores");
    public static final TagKey<Block> SPORES_CAN_GROW = ModBlockTags.of("spores_can_grow");

    public static final TagKey<Block> SHEAR_MINEABLE = ModBlockTags.ofCommon("shear_mineable");

    private static TagKey<Block> of(String id) {
        return TagKey.of(RegistryKeys.BLOCK, new Identifier(Constants.MOD_ID, id));
    }

    private static TagKey<Block> ofCommon(String id) {
        return TagKey.of(RegistryKeys.BLOCK, new Identifier(Constants.COMMON_ID, id));
    }
}
