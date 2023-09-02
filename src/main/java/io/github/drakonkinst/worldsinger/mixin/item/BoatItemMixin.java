package io.github.drakonkinst.worldsinger.mixin.item;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.drakonkinst.worldsinger.api.ModApi;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SilverLinedComponent;
import io.github.drakonkinst.worldsinger.entity.SilverLinedEntityData;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.SilverLined;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BoatItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(BoatItem.class)
public abstract class BoatItemMixin extends Item implements SilverLined {

    public BoatItemMixin(Settings settings) {
        super(settings);
    }

    @Unique
    private int silverDurability;

    @ModifyVariable(method = "use", at = @At(value = "STORE"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;", ordinal = 1), to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;setVariant(Lnet/minecraft/entity/vehicle/BoatEntity$Type;)V")))
    private BoatEntity addDataToEntity(BoatEntity entity, @Local PlayerEntity user,
            @Local Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        this.copySilverDataFromItemToEntity(itemStack, entity);
        return entity;
    }

    @Unique
    private void copySilverDataFromItemToEntity(ItemStack itemStack, BoatEntity entity) {
        SilverLined silverItemData = ModApi.SILVER_LINED_ITEM.find(itemStack, null);
        if (silverItemData == null) {
            ModConstants.LOGGER.error("Expected to find silver data for boat item");
            return;
        }
        int silverDurability = silverItemData.worldsinger$getSilverDurability();
        if (silverDurability <= 0) {
            return;
        }
        SilverLinedComponent silverEntityData = ModComponents.SILVER_LINED_ENTITY.get(entity);
        silverEntityData.setSilverDurability(silverDurability);
    }

    @Override
    public void worldsinger$setSilverDurability(int durability) {
        this.silverDurability = durability;
    }

    @Override
    public int worldsinger$getSilverDurability() {
        return silverDurability;
    }

    @Override
    public int worldsinger$getMaxSilverDurability() {
        return SilverLinedEntityData.MAX_BOAT_SILVER_DURABILITY;
    }
}
