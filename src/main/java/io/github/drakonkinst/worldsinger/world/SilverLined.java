package io.github.drakonkinst.worldsinger.world;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.api.ModApi;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SilverLinedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public interface SilverLined {

    static void transferSilverLinedDataFromEntityToItemStack(Entity entity, ItemStack itemStack) {
        SilverLinedComponent silverEntityData = ModComponents.SILVER_LINED.get(entity);
        int silverDurability = silverEntityData.getSilverDurability();
        if (silverDurability > 0) {
            SilverLined silverItemData = ModApi.SILVER_LINED_ITEM.find(itemStack, null);
            if (silverItemData != null) {
                silverItemData.setSilverDurability(silverDurability);
            } else {
                Worldsinger.LOGGER.error("Expected to find silver data for new boat item");
            }
        }
    }

    int getSilverDurability();

    int getMaxSilverDurability();

    void setSilverDurability(int durability);
}
