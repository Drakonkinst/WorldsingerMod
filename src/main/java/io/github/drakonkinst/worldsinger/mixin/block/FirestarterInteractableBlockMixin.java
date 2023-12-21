package io.github.drakonkinst.worldsinger.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.item.ModItemTags;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.TntBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ TntBlock.class, CandleCakeBlock.class })
public abstract class FirestarterInteractableBlockMixin {

    @WrapOperation(method = "onUseWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean checkForNewFirestarters(ItemStack instance, Item originalItem,
            Operation<Boolean> original) {
        if (original.call(instance, originalItem)) {
            return true;
        }
        if (originalItem.equals(Items.FLINT_AND_STEEL)) {
            return instance.isIn(ModItemTags.FLINT_AND_STEEL_VARIANTS);
        }
        return false;
    }
}
