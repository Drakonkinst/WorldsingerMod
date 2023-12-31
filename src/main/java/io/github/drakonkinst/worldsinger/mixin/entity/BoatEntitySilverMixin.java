package io.github.drakonkinst.worldsinger.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SilverLinedComponent;
import io.github.drakonkinst.worldsinger.cosmere.SilverLined;
import io.github.drakonkinst.worldsinger.item.ModItemTags;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatEntity.class)
public abstract class BoatEntitySilverMixin extends VehicleEntity {

    @Unique
    private static final int SILVER_REPAIR_AMOUNT = 625;

    public BoatEntitySilverMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void addSilverLining(PlayerEntity player, Hand hand,
            CallbackInfoReturnable<ActionResult> cir) {
        SilverLinedComponent silverData = ModComponents.SILVER_LINED.get(this);
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isIn(ModItemTags.SILVER_INGOTS)
                && silverData.getSilverDurability() < silverData.getMaxSilverDurability()) {
            silverData.setSilverDurability(silverData.getSilverDurability() + SILVER_REPAIR_AMOUNT);
            float pitch = 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f;
            this.playSound(ModSoundEvents.ENTITY_BOAT_LINE_SILVER, 1.0f, pitch);
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            cir.setReturnValue(ActionResult.success(this.getWorld().isClient()));
        }
    }

    @ModifyReturnValue(method = "getPickBlockStack", at = @At("RETURN"))
    private ItemStack dropWithSilverData(ItemStack itemStack) {
        return this.addSilverData(itemStack);
    }

    @Unique
    private ItemStack addSilverData(ItemStack itemStack) {
        SilverLined.transferSilverLinedDataFromEntityToItemStack(this, itemStack);
        return itemStack;
    }
}
