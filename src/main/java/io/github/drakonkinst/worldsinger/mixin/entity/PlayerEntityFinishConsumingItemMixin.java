package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.event.FinishConsumingItemCallback;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityFinishConsumingItemMixin extends LivingEntity {

    protected PlayerEntityFinishConsumingItemMixin(EntityType<? extends LivingEntity> entityType,
            World world) {
        super(entityType, world);
    }

    @Inject(method = "eatFood", at = @At("HEAD"))
    private void onFinishConsumingItem(World world, ItemStack stack,
            CallbackInfoReturnable<ItemStack> cir) {
        FinishConsumingItemCallback.EVENT.invoker().onConsume(this, stack);
    }
}
