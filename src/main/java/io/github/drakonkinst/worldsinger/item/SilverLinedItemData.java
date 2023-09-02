package io.github.drakonkinst.worldsinger.item;

import io.github.drakonkinst.worldsinger.util.SilverLined;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public abstract class SilverLinedItemData implements SilverLined {

    public static final String NBT_KEY = "SilverLined";

    protected final ItemStack stack;

    public SilverLinedItemData(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void setSilverDurability(int durability) {
        durability = Math.max(0, Math.min(durability, this.getMaxSilverDurability()));
        stack.getOrCreateNbt().putInt(NBT_KEY, durability);
    }

    @Override
    public int getSilverDurability() {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(NBT_KEY, NbtElement.INT_TYPE)) {
            return 0;
        }
        return nbt.getInt(NBT_KEY);
    }

    public ItemStack getStack() {
        return stack;
    }
}
