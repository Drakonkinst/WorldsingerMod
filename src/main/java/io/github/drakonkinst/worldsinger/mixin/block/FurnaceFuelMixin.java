package io.github.drakonkinst.worldsinger.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.item.ModItems;
import java.util.Map;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceFuelMixin {

    @Unique
    // Using the time required to cook most items
    private static final int NUM_ITEMS_TO_TICKS = 20 * 10;

    @ModifyReturnValue(method = "createFuelTimeMap", at = @At("RETURN"))
    private static Map<Item, Integer> addCustomFuel(Map<Item, Integer> map) {
        FurnaceFuelMixin.addFuel(map, ModItems.SUNLIGHT_SPORES_BUCKET, 100);
        FurnaceFuelMixin.addFuel(map, ModItems.SUNLIGHT_SPORES_BOTTLE, 8);
        return map;
    }

    @Unique
    private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int numItems) {
        Item item2 = item.asItem();
        if (FurnaceFuelMixin.isNonFlammableWood(item2)) {
            return;
        }
        fuelTimes.put(item2, numItems * NUM_ITEMS_TO_TICKS);
    }

    @Unique
    private static boolean isNonFlammableWood(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.NON_FLAMMABLE_WOOD);
    }

    @Unique
    private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int numItems) {
        for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(tag)) {
            if (FurnaceFuelMixin.isNonFlammableWood(registryEntry.value())) {
                continue;
            }
            fuelTimes.put(registryEntry.value(), numItems * NUM_ITEMS_TO_TICKS);
        }
    }
}
