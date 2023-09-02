package io.github.drakonkinst.worldsinger.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.api.ModApi;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SilverLinedComponent;
import io.github.drakonkinst.worldsinger.item.ModItemTags;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.SilverLined;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatEntity.class)
public abstract class BoatEntitySilverMixin extends Entity {

    @Shadow
    public abstract Item asItem();

    @Unique
    private static final int SILVER_REPAIR_AMOUNT = 625;

    public BoatEntitySilverMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void addSilverLining(PlayerEntity player, Hand hand,
            CallbackInfoReturnable<ActionResult> cir) {
        SilverLinedComponent silverData = ModComponents.SILVER_LINED_ENTITY.get(this);
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isIn(ModItemTags.SILVER_INGOTS)
                && silverData.getSilverDurability() < silverData.getMaxSilverDurability()) {
            silverData.setSilverDurability(
                    silverData.getSilverDurability() + SILVER_REPAIR_AMOUNT);
            float pitch = 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f;
            // TODO: Temp sound
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, pitch);
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }
            cir.setReturnValue(ActionResult.success(this.getWorld().isClient()));
        }
    }

    @Inject(method = "dropItems", at = @At("HEAD"), cancellable = true)
    private void dropWithSilverData(DamageSource source, CallbackInfo ci) {
        this.dropStack(this.createItemStack());
        ci.cancel();
    }

    @ModifyReturnValue(method = "getPickBlockStack", at = @At("RETURN"))
    private ItemStack dropWithSilverData(ItemStack itemStack) {
        return this.addSilverData(itemStack);
    }

    @Unique
    private ItemStack createItemStack() {
        return this.addSilverData(new ItemStack(this.asItem()));
    }

    @Unique
    private ItemStack addSilverData(ItemStack itemStack) {
        SilverLinedComponent silverEntityData = ModComponents.SILVER_LINED_ENTITY.get(this);
        int silverDurability = silverEntityData.getSilverDurability();
        if (silverDurability > 0) {
            SilverLined silverItemData = ModApi.SILVER_LINED_ITEM.find(itemStack, null);
            if (silverItemData != null) {
                silverItemData.setSilverDurability(silverDurability);
            } else {
                ModConstants.LOGGER.error("Expected to find silver data for new boat item");
            }
        }
        return itemStack;
    }
}
