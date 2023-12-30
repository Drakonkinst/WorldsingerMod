package io.github.drakonkinst.worldsinger.mixin.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.item.KnifeItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Enchantment.class)
public class EnchantmentTargetMixin {

    @Shadow
    @Final
    public EnchantmentTarget target;

    @ModifyReturnValue(method = "isAcceptableItem", at = @At("RETURN"))
    private boolean considerKnivesAsWeapons(boolean original, ItemStack stack) {
        if (original) {
            return true;
        }

        if (this.target == EnchantmentTarget.WEAPON) {
            return stack.getItem() instanceof KnifeItem;
        }
        return false;
    }
}
