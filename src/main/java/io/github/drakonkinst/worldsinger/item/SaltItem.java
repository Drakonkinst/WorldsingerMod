package io.github.drakonkinst.worldsinger.item;

import io.github.drakonkinst.worldsinger.cosmere.lumar.AetherSpores;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SaltItem extends Item {

    public SaltItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient) {
            AetherSpores.clearAllSporeEffects(user);
        }
        return super.finishUsing(stack, world, user);
    }
}
