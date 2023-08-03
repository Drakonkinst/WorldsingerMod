package io.github.drakonkinst.examplemod;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item DEFAULT_CUBE = ModItems.register(
            new DefaultCubeItem(new FabricItemSettings()), "default_cube"
    );

    private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(DEFAULT_CUBE))
            .displayName(Text.translatable("itemGroup.tutorial.test_group"))
            .build();

    public static <T extends Item> T register(T item, String id) {
        Identifier itemId = new Identifier(ExampleMod.MOD_ID, id);
        T registeredItem = Registry.register(Registries.ITEM, itemId, item);
        return registeredItem;
    }

    public static void initialize() {
        Identifier moddedItemsIdentifier = new Identifier("examplemod", "modded_items");
        Registry.register(Registries.ITEM_GROUP, moddedItemsIdentifier, ITEM_GROUP);
        RegistryKey<ItemGroup> moddedItemsItemGroupRegistryKey = RegistryKey.of(RegistryKeys.ITEM_GROUP, moddedItemsIdentifier);

        // ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> {
        //     itemGroup.add(ModItems.DEFAULT_CUBE);
        // });

        ItemGroupEvents.modifyEntriesEvent(moddedItemsItemGroupRegistryKey).register((itemGroup) -> {
            itemGroup.add(ModItems.DEFAULT_CUBE);
        });
    }

}
