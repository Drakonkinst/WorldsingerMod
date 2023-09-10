package io.github.drakonkinst.worldsinger.world;

import io.github.drakonkinst.worldsinger.datatable.DataTable;
import io.github.drakonkinst.worldsinger.item.ModItemTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class MetalQueryManager {

    private static final int HELD_ITEM_METAL_VALUE = 1;
    private static final int USING_SHIELD_BONUS = 2;

    public static int getIronContentForEntity(Entity entity, DataTable entityDataTable,
            DataTable armorDataTable) {
        return MetalQueryManager.getMetalContentForEntity(entity, Metals.IRON, entityDataTable,
                armorDataTable);
    }

    public static int getSteelContentForEntity(Entity entity, DataTable entityDataTable,
            DataTable armorDataTable) {
        return MetalQueryManager.getMetalContentForEntity(entity, Metals.STEEL, entityDataTable,
                armorDataTable);
    }

    public static int getMetalContentForEntity(Entity entity, Metal metal,
            DataTable entityDataTable, DataTable armorDataTable) {
        int ironContent = 0;

        if (entity.getType().isIn(metal.getEntityTypeTag())) {
            ironContent += entityDataTable.getIntForEntity(entity);
        }

        for (ItemStack itemStack : entity.getHandItems()) {
            if (!itemStack.isEmpty() && itemStack.isIn(metal.getItemTag())) {
                ironContent += HELD_ITEM_METAL_VALUE;
            }
        }

        for (ItemStack itemStack : entity.getArmorItems()) {
            if (!itemStack.isEmpty() && itemStack.isIn(metal.getItemTag())) {
                ironContent += armorDataTable.getIntForItem(itemStack.getItem());
            }
        }

        if (entity instanceof PlayerEntity playerEntity) {
            ItemStack activeItem = playerEntity.getActiveItem();
            if (!activeItem.isEmpty() && activeItem.isIn(metal.getItemTag())) {
                if (activeItem.isIn(ModItemTags.SHIELDS)) {
                    ironContent += USING_SHIELD_BONUS;
                }
            }
        }

        return ironContent;
    }
}
