package io.github.drakonkinst.worldsinger.mixin.client;

import io.github.drakonkinst.worldsinger.cosmere.lumar.AetherSpores;
import io.github.drakonkinst.worldsinger.item.ModItems;
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
                (stack, tintIndex) -> tintIndex > 0 ? -1 : AetherSpores.getBottleColor(stack),
                ModItems.DEAD_SPORES_BOTTLE,
                ModItems.VERDANT_SPORES_BOTTLE,
                ModItems.CRIMSON_SPORES_BOTTLE,
                ModItems.ZEPHYR_SPORES_BOTTLE,
                ModItems.SUNLIGHT_SPORES_BOTTLE,
                ModItems.ROSEITE_SPORES_BOTTLE,
                ModItems.MIDNIGHT_SPORES_BOTTLE,
                ModItems.DEAD_SPORES_SPLASH_BOTTLE,
                ModItems.VERDANT_SPORES_SPLASH_BOTTLE,
                ModItems.CRIMSON_SPORES_SPLASH_BOTTLE,
                ModItems.ZEPHYR_SPORES_SPLASH_BOTTLE,
                ModItems.SUNLIGHT_SPORES_SPLASH_BOTTLE,
                ModItems.ROSEITE_SPORES_SPLASH_BOTTLE,
                ModItems.MIDNIGHT_SPORES_SPLASH_BOTTLE
        );
        cir.setReturnValue(itemColors);
    }
}
