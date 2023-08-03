package io.github.drakonkinst.examplemod;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModBlocks {

    private ModBlocks() {}

    public static final Block DISCORD_BLOCK = register(new Block(FabricBlockSettings.create().strength(4.0f)), "discord_block", true);

    public static <T extends Block> T register(T block, String id, boolean shouldRegisterItem) {
        Identifier blockId = new Identifier(ExampleMod.MOD_ID, id);

        if(shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, blockId, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockId, block);
    }

    public static void initialize() {
        Identifier moddedItemsIdentifier = new Identifier(ExampleMod.MOD_ID, "modded_items");
        RegistryKey<ItemGroup> moddedItemsItemGroupKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, moddedItemsIdentifier);
        ItemGroupEvents.modifyEntriesEvent(moddedItemsItemGroupKey).register((itemGroup) -> {
            itemGroup.add(ModBlocks.DISCORD_BLOCK.asItem());
        });
    }

}
