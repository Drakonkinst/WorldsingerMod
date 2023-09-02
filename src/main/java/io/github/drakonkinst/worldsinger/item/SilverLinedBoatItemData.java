package io.github.drakonkinst.worldsinger.item;

import io.github.drakonkinst.worldsinger.entity.SilverLinedBoatEntityData;
import net.minecraft.item.ItemStack;

public class SilverLinedBoatItemData extends SilverLinedItemData {

    public SilverLinedBoatItemData(ItemStack stack) {
        super(stack);
    }

    @Override
    public int getMaxSilverDurability() {
        return SilverLinedBoatEntityData.MAX_DURABILITY;
    }
}
