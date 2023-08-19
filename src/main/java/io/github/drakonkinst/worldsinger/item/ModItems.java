package io.github.drakonkinst.worldsinger.item;

import io.github.drakonkinst.worldsinger.Constants;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItems {

    private ModItems() {
    }

    public static final Item DEFAULT_CUBE = ModItems.register(
            new DefaultCubeItem(new FabricItemSettings()), "default_cube"
    );
    public static final Item BALLS = ModItems.register( // By popular vote
            new Item(new FabricItemSettings()
                    // Probably best to make a separate ModFoodComponents static class and have these all in one place
                    .food(new FoodComponent.Builder()
                            .alwaysEdible()
                            .hunger(4)
                            .saturationModifier(0.3f)
                            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER,
                                    15 * Constants.SECONDS_TO_TICKS, 1), 1.0f)
                            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA,
                                    5 * Constants.SECONDS_TO_TICKS, 1), 1.0f)
                            .build())),
            "balls"
    );
    // public static final Item VERDANT_SPORES_BUCKET = ModItems.register(
    //         new BucketItem(ModFluids.VERDANT_SPORES, new FabricItemSettings()
    //                 .recipeRemainder(Items.BUCKET).maxCount(1)), "verdant_spores_bucket"
    // );
    public static final Item VERDANT_SPORES_BUCKET = ModItems.register(
            new AetherSporeBucketItem(ModBlocks.VERDANT_SPORE_BLOCK,
                    SoundEvents.BLOCK_POWDER_SNOW_PLACE,
                    new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1)),
            "verdant_spores_bucket");

    private static final ItemGroup MODDED_ITEMS_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(DEFAULT_CUBE))
            .displayName(Text.translatable("itemGroup.tutorial.test_group"))
            .build();

    public static <T extends Item> T register(T item, String id) {
        Identifier itemId = new Identifier(Constants.MOD_ID, id);
        return Registry.register(Registries.ITEM, itemId, item);
    }

    public static void initialize() {
        // Custom item group
        Identifier moddedItemsIdentifier = new Identifier(Constants.MOD_ID, "modded_items");
        Registry.register(Registries.ITEM_GROUP, moddedItemsIdentifier, MODDED_ITEMS_GROUP);
        RegistryKey<ItemGroup> moddedItemsItemGroupKey = RegistryKey.of(RegistryKeys.ITEM_GROUP,
                moddedItemsIdentifier);

        // ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> {
        //     itemGroup.add(ModItems.DEFAULT_CUBE);
        // });

        ItemGroupEvents.modifyEntriesEvent(moddedItemsItemGroupKey).register((itemGroup) -> {
            itemGroup.add(ModItems.DEFAULT_CUBE);
            itemGroup.add(ModItems.BALLS);
            itemGroup.add(ModBlocks.DISCORD_BLOCK.asItem());
            itemGroup.add(ModItems.VERDANT_SPORES_BUCKET);
            itemGroup.add(ModBlocks.VERDANT_SPORE_BLOCK.asItem());
            itemGroup.add(ModBlocks.VERDANT_VINE_BLOCK.asItem());
            itemGroup.add(ModBlocks.VERDANT_VINE_BRANCH.asItem());
            itemGroup.add(ModBlocks.VERDANT_VINE_SNARE.asItem());
            itemGroup.add(ModBlocks.TWISTING_VERDANT_VINES.asItem());
        });
    }

}
