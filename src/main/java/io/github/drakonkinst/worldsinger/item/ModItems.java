package io.github.drakonkinst.worldsinger.item;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.util.Constants;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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

    // Items
    public static final Item DEAD_SPORES_BUCKET = ModItems.register("dead_spores_bucket",
            new AetherSporeBucketItem(ModBlocks.DEAD_SPORE_BLOCK,
                    SoundEvents.BLOCK_POWDER_SNOW_PLACE,
                    new FabricItemSettings()
                            .recipeRemainder(Items.BUCKET)
                            .maxCount(1)));
    public static final Item VERDANT_SPORES_BUCKET = ModItems.register(
            "verdant_spores_bucket", new AetherSporeBucketItem(ModBlocks.VERDANT_SPORE_BLOCK,
                    SoundEvents.BLOCK_POWDER_SNOW_PLACE,
                    new FabricItemSettings()
                            .recipeRemainder(Items.BUCKET)
                            .maxCount(1)));
    public static final Item SALT = ModItems.register("salt",
            new SaltItem(new FabricItemSettings()));

    // Item Groups
    private static final ItemGroup WORLDSINGER_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.VERDANT_VINE_SNARE.asItem()))
            .displayName(Text.translatable("itemGroup.worldsinger.worldsinger"))
            .build();

    public static <T extends Item> T register(String id, T item) {
        Identifier itemId = new Identifier(Constants.MOD_ID, id);
        return Registry.register(Registries.ITEM, itemId, item);
    }

    public static void initialize() {
        // Custom item group
        Identifier moddedItemsIdentifier = new Identifier(Constants.MOD_ID, "worldsinger");
        Registry.register(Registries.ITEM_GROUP, moddedItemsIdentifier, WORLDSINGER_ITEM_GROUP);
        RegistryKey<ItemGroup> moddedItemsItemGroupKey = RegistryKey.of(RegistryKeys.ITEM_GROUP,
                moddedItemsIdentifier);

        ItemGroupEvents.modifyEntriesEvent(moddedItemsItemGroupKey).register((itemGroup) -> {
            itemGroup.add(ModItems.VERDANT_SPORES_BUCKET);
            itemGroup.add(ModBlocks.VERDANT_SPORE_BLOCK.asItem());
            itemGroup.add(ModBlocks.VERDANT_VINE_BLOCK.asItem());
            itemGroup.add(ModBlocks.VERDANT_VINE_BRANCH.asItem());
            itemGroup.add(ModBlocks.VERDANT_VINE_SNARE.asItem());
            itemGroup.add(ModBlocks.TWISTING_VERDANT_VINES.asItem());
            itemGroup.add(ModItems.DEAD_SPORES_BUCKET);
            itemGroup.add(ModBlocks.DEAD_SPORE_BLOCK.asItem());
            itemGroup.add(ModBlocks.DEAD_VERDANT_VINE_BLOCK.asItem());
            itemGroup.add(ModBlocks.DEAD_VERDANT_VINE_BRANCH.asItem());
            itemGroup.add(ModBlocks.DEAD_VERDANT_VINE_SNARE.asItem());
            itemGroup.add(ModBlocks.DEAD_TWISTING_VERDANT_VINES.asItem());
            itemGroup.add(ModBlocks.SALTSTONE.asItem());
            itemGroup.add(ModItems.SALT);
        });
    }

    private ModItems() {}
}
