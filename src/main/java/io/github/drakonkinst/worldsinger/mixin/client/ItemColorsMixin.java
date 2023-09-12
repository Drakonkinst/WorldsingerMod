package io.github.drakonkinst.worldsinger.mixin.client;

import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColors.class)
public abstract class ItemColorsMixin {

    @Inject(method = "create", at = @At("RETURN"), cancellable = true)
    private static void addSporePotionItemColors(BlockColors blockColors,
            CallbackInfoReturnable<ItemColors> cir) {
        ItemColors itemColors = cir.getReturnValue();
        itemColors.register(
                (stack, tintIndex) -> tintIndex > 0 ? -1 : AetherSporeType.getBottleColor(stack),
                ModItems.VERDANT_SPORES_BOTTLE, ModItems.DEAD_SPORES_BOTTLE,
                ModItems.VERDANT_SPORES_SPLASH_BOTTLE, ModItems.DEAD_SPORES_SPLASH_BOTTLE);
        cir.setReturnValue(itemColors);
    }
}