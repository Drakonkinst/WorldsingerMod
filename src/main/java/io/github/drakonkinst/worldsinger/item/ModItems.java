package io.github.drakonkinst.worldsinger.item;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.material.ModArmorMaterials;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
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
    public static final Item RAW_SILVER = ModItems.register("raw_silver",
            new SaltItem(new FabricItemSettings()));
    public static final Item SILVER_INGOT = ModItems.register("silver_ingot",
            new Item(new FabricItemSettings()));
    public static final Item SILVER_NUGGET = ModItems.register("silver_nugget",
            new Item(new FabricItemSettings()));

    // Steel
    public static final Item CRUDE_IRON = ModItems.register("crude_iron",
            new Item(new FabricItemSettings()));
    public static final Item STEEL_INGOT = ModItems.register("steel_ingot",
            new Item(new FabricItemSettings()));
    public static final Item STEEL_NUGGET = ModItems.register("steel_nugget",
            new Item(new FabricItemSettings()));
    public static final Item STEEL_HELMET = ModItems.register("steel_helmet", new ArmorItem(
            ModArmorMaterials.STEEL, ArmorItem.Type.HELMET, new FabricItemSettings()));
    public static final Item STEEL_CHESTPLATE = ModItems.register("steel_chestplate", new ArmorItem(
            ModArmorMaterials.STEEL, ArmorItem.Type.CHESTPLATE, new FabricItemSettings()));
    public static final Item STEEL_LEGGINGS = ModItems.register("steel_leggings", new ArmorItem(
            ModArmorMaterials.STEEL, ArmorItem.Type.LEGGINGS, new FabricItemSettings()));
    public static final Item STEEL_BOOTS = ModItems.register("steel_boots", new ArmorItem(
            ModArmorMaterials.STEEL, ArmorItem.Type.BOOTS, new FabricItemSettings()));

    // Item Groups
    private static final ItemGroup WORLDSINGER_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.VERDANT_VINE_SNARE))
            .displayName(Text.translatable("itemGroup.worldsinger.worldsinger"))
            .build();

    public static <T extends Item> T register(String id, T item) {
        Identifier itemId = new Identifier(ModConstants.MOD_ID, id);
        return Registry.register(Registries.ITEM, itemId, item);
    }

    public static void initialize() {
        // Custom item group
        Identifier moddedItemsIdentifier = new Identifier(ModConstants.MOD_ID, "worldsinger");
        Registry.register(Registries.ITEM_GROUP, moddedItemsIdentifier, WORLDSINGER_ITEM_GROUP);
        RegistryKey<ItemGroup> moddedItemsItemGroupKey = RegistryKey.of(RegistryKeys.ITEM_GROUP,
                moddedItemsIdentifier);

        ItemGroupEvents.modifyEntriesEvent(moddedItemsItemGroupKey).register((itemGroup) -> {
            itemGroup.add(ModItems.VERDANT_SPORES_BUCKET);
            itemGroup.add(ModBlocks.VERDANT_SPORE_BLOCK);
            itemGroup.add(ModBlocks.VERDANT_VINE_BLOCK);
            itemGroup.add(ModBlocks.VERDANT_VINE_BRANCH);
            itemGroup.add(ModBlocks.VERDANT_VINE_SNARE);
            itemGroup.add(ModBlocks.TWISTING_VERDANT_VINES);
            itemGroup.add(ModItems.DEAD_SPORES_BUCKET);
            itemGroup.add(ModBlocks.DEAD_SPORE_BLOCK);
            itemGroup.add(ModBlocks.DEAD_VERDANT_VINE_BLOCK);
            itemGroup.add(ModBlocks.DEAD_VERDANT_VINE_BRANCH);
            itemGroup.add(ModBlocks.DEAD_VERDANT_VINE_SNARE);
            itemGroup.add(ModBlocks.DEAD_TWISTING_VERDANT_VINES);
            itemGroup.add(ModBlocks.SALTSTONE);
            itemGroup.add(ModBlocks.SALTSTONE_SALT_ORE);
            itemGroup.add(ModBlocks.SALTSTONE_SILVER_ORE);
            itemGroup.add(ModItems.SALT);
            itemGroup.add(ModBlocks.SALT_BLOCK);

            // Silver
            itemGroup.add(ModItems.RAW_SILVER);
            itemGroup.add(ModItems.SILVER_INGOT);
            itemGroup.add(ModItems.SILVER_NUGGET);
            itemGroup.add(ModBlocks.SILVER_BLOCK);
            itemGroup.add(ModBlocks.RAW_SILVER_BLOCK);

            // Steel
            itemGroup.add(ModItems.CRUDE_IRON);
            itemGroup.add(ModItems.STEEL_INGOT);
            itemGroup.add(ModItems.STEEL_NUGGET);
            itemGroup.add(ModBlocks.STEEL_BLOCK);
            itemGroup.add(ModItems.STEEL_HELMET);
            itemGroup.add(ModItems.STEEL_CHESTPLATE);
            itemGroup.add(ModItems.STEEL_LEGGINGS);
            itemGroup.add(ModItems.STEEL_BOOTS);
            itemGroup.add(ModBlocks.STEEL_ANVIL);
            itemGroup.add(ModBlocks.CHIPPED_STEEL_ANVIL);
            itemGroup.add(ModBlocks.DAMAGED_STEEL_ANVIL);
        });
    }

    private ModItems() {}
}
