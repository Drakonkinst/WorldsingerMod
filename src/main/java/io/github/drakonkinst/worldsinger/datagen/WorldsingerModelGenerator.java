package io.github.drakonkinst.worldsinger.datagen;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemConvertible;

// Datagen is very limited and only really works for the simplest items and blocks for now.
// Still, it's worthwhile and saves some work.
public class WorldsingerModelGenerator extends FabricModelProvider {

    public WorldsingerModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerSimpleCubeBlocks(blockStateModelGenerator, new Block[] {
                ModBlocks.CRIMSON_GROWTH,
                ModBlocks.DEAD_CRIMSON_GROWTH,
                ModBlocks.DEAD_SPORE_BLOCK,
                ModBlocks.CRIMSON_SPORE_BLOCK,
                ModBlocks.DEEPSLATE_SILVER_ORE,
                ModBlocks.MAGMA_VENT,
                ModBlocks.MIDNIGHT_SPORE_BLOCK,
                ModBlocks.RAW_SILVER_BLOCK,
                ModBlocks.ROSEITE_SPORE_BLOCK,
                ModBlocks.SALT_BLOCK,
                ModBlocks.SALTSTONE,
                ModBlocks.SALTSTONE_SALT_ORE,
                ModBlocks.SILVER_BLOCK,
                ModBlocks.SILVER_ORE,
                ModBlocks.STEEL_BLOCK,
                ModBlocks.SUNLIGHT_SPORE_BLOCK,
                ModBlocks.VERDANT_SPORE_BLOCK,
                ModBlocks.ZEPHYR_SPORE_BLOCK
        });
        BlockStateModelGenerator.BlockTexturePool roseiteTexturePool = blockStateModelGenerator.registerCubeAllModelTexturePool(
                ModBlocks.ROSEITE_BLOCK);
        roseiteTexturePool.stairs(ModBlocks.ROSEITE_STAIRS);
        roseiteTexturePool.slab(ModBlocks.ROSEITE_SLAB);
    }

    private void registerSimpleCubeBlocks(BlockStateModelGenerator blockStateModelGenerator,
            Block[] blocks) {
        for (Block block : blocks) {
            blockStateModelGenerator.registerSimpleCubeAll(block);
        }
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        registerGeneratedItems(itemModelGenerator, new ItemConvertible[] {
                // Can't do blocks because their path leads to block textures.
                // ModBlocks.CRIMSON_SNARE,
                // ModBlocks.DEAD_CRIMSON_SNARE,
                // ModBlocks.CRIMSON_SPINES,
                // ModBlocks.DEAD_CRIMSON_SPINES,
                // ModBlocks.TALL_CRIMSON_SPINES,
                // ModBlocks.DEAD_TALL_CRIMSON_SPINES,
                // ModBlocks.VERDANT_VINE_SNARE,
                // ModBlocks.DEAD_VERDANT_VINE_SNARE,
                // ModBlocks.TWISTING_VERDANT_VINES,
                // ModBlocks.DEAD_TWISTING_VERDANT_VINES,
                ModItems.VERDANT_VINE,
                ModItems.CRUDE_IRON,
                ModItems.RAW_SILVER,
                ModItems.SILVER_NUGGET,
                ModItems.SILVER_INGOT,
                ModItems.SALT,
                ModItems.STEEL_NUGGET,
                ModItems.STEEL_INGOT,
                ModItems.DEAD_SPORES_BUCKET,
                ModItems.VERDANT_SPORES_BUCKET,
                ModItems.CRIMSON_SPORES_BUCKET,
                ModItems.ZEPHYR_SPORES_BUCKET,
                ModItems.SUNLIGHT_SPORES_BUCKET,
                ModItems.ROSEITE_SPORES_BUCKET,
                ModItems.MIDNIGHT_SPORES_BUCKET,
                ModItems.FLINT_AND_IRON,
                ModItems.QUARTZ_AND_IRON,
                ModItems.QUARTZ_AND_STEEL
        });
        registerHandheldItems(itemModelGenerator, new ItemConvertible[] {
                ModItems.CRIMSON_SPINE,
                ModItems.STEEL_AXE,
                ModItems.STEEL_PICKAXE,
                ModItems.STEEL_HOE,
                ModItems.STEEL_SHOVEL,
                ModItems.STEEL_SWORD
        });

        // Adds armor trim models as well, which we didn't have before
        itemModelGenerator.registerArmor((ArmorItem) ModItems.STEEL_HELMET);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.STEEL_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.STEEL_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.STEEL_BOOTS);
    }

    private void registerGeneratedItems(ItemModelGenerator itemModelGenerator,
            ItemConvertible[] items) {
        for (ItemConvertible item : items) {
            itemModelGenerator.register(item.asItem(), Models.GENERATED);
        }
    }

    private void registerHandheldItems(ItemModelGenerator itemModelGenerator,
            ItemConvertible[] items) {
        for (ItemConvertible item : items) {
            itemModelGenerator.register(item.asItem(), Models.HANDHELD);
        }
    }

}
