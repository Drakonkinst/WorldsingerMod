package io.github.drakonkinst.worldsinger.mixin.screen;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.item.ModItemTags;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.screen.BrewingStandScreenHandler$FuelSlot")
public class BrewingStandScreenHandlerFuelSlotMixin {

    @ModifyReturnValue(method = "matches", at = @At("RETURN"))
    private static boolean allowCustomFuel(boolean originalValue, ItemStack stack) {
        return originalValue || stack.isIn(ModItemTags.BREWING_STAND_FUELS);
    }
}
