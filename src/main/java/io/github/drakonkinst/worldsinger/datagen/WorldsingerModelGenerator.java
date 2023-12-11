package io.github.drakonkinst.worldsinger.datagen;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class WorldsingerModelGenerator extends FabricModelProvider {

    public WorldsingerModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CRIMSON_GROWTH);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEAD_CRIMSON_GROWTH);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEAD_SPORE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DEEPSLATE_SILVER_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MAGMA_VENT);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MIDNIGHT_SPORE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_SILVER_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ROSEITE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ROSEITE_SPORE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SALT_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SALTSTONE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SALTSTONE_SALT_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SILVER_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SILVER_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.STEEL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.SUNLIGHT_SPORE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.VERDANT_SPORE_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ZEPHYR_SPORE_BLOCK);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}
