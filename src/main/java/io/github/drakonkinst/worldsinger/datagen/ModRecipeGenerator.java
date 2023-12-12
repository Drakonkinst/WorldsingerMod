package io.github.drakonkinst.worldsinger.datagen;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;

public class ModRecipeGenerator extends FabricRecipeProvider {

    public ModRecipeGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        offerStairsRecipe(exporter, ModBlocks.ROSEITE_STAIRS, true, ModBlocks.ROSEITE_BLOCK);
        offerSlabRecipe(exporter, ModBlocks.ROSEITE_SLAB, true, ModBlocks.ROSEITE_BLOCK);
    }

    private void offerStairsRecipe(RecipeExporter exporter, ItemConvertible stairsOutput,
            boolean addStonecutterRecipe, ItemConvertible... input) {
        CraftingRecipeJsonBuilder recipeBuilder = RecipeProvider.createStairsRecipe(stairsOutput,
                Ingredient.ofItems(input));
        for (ItemConvertible inputItem : input) {
            String criterionName = "has_" + Registries.ITEM.getId(inputItem.asItem()).getPath();
            recipeBuilder = recipeBuilder.criterion(criterionName,
                    RecipeProvider.conditionsFromItem(inputItem));
        }
        recipeBuilder.offerTo(exporter);

        if (addStonecutterRecipe) {
            for (ItemConvertible inputItem : input) {
                RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS,
                        stairsOutput, inputItem);
            }
        }
    }

    private void offerSlabRecipe(RecipeExporter exporter, ItemConvertible slabOutput,
            boolean addStonecutterRecipe, ItemConvertible... input) {
        CraftingRecipeJsonBuilder recipeBuilder = RecipeProvider.createSlabRecipe(
                RecipeCategory.BUILDING_BLOCKS, slabOutput, Ingredient.ofItems(input));
        for (ItemConvertible inputItem : input) {
            String criterionName = "has_" + Registries.ITEM.getId(inputItem.asItem()).getPath();
            recipeBuilder = recipeBuilder.criterion(criterionName,
                    RecipeProvider.conditionsFromItem(inputItem));
        }
        recipeBuilder.offerTo(exporter);

        if (addStonecutterRecipe) {
            for (ItemConvertible inputItem : input) {
                RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS,
                        slabOutput, inputItem, 2);
            }
        }
    }
}
