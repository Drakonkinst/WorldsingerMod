package io.github.drakonkinst.worldsinger.mixin.item;

import io.github.drakonkinst.worldsinger.event.FinishConsumingItemCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.HoneyBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.item.PotionItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ HoneyBottleItem.class, MilkBucketItem.class, PotionItem.class })
public abstract class DrinkableItemFinishConsumingItemMixin extends Item {

    public DrinkableItemFinishConsumingItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void onFinishConsumingItem(ItemStack stack, World world, LivingEntity user,
            CallbackInfoReturnable<ItemStack> cir) {
        FinishConsumingItemCallback.EVENT.invoker().onConsume(user, stack);
    }

}
