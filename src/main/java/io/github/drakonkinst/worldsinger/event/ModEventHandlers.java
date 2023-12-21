package io.github.drakonkinst.worldsinger.event;

import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.item.ModItemTags;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

public final class ModEventHandlers {

    // TODO: Consider moving each event to its relevant feature, i.e. a ThirstManager
    public static void register() {
        FinishConsumingItemCallback.EVENT.register(((entity, stack) -> {
            if (entity instanceof PlayerEntity player) {
                ModComponents.THIRST_MANAGER.get(player).drink(stack.getItem(), stack);

                // Status effects should only be added on server side
                if (!entity.getWorld().isClient()) {
                    if (stack.isIn(ModItemTags.ALWAYS_GIVE_THIRST)) {
                        player.addStatusEffect(
                                new StatusEffectInstance(ModStatusEffects.THIRST, 600, 0));
                    }
                    if (stack.isIn(ModItemTags.CHANCE_TO_GIVE_THIRST)
                            && entity.getWorld().getRandom().nextInt(5) != 0) {
                        player.addStatusEffect(
                                new StatusEffectInstance(ModStatusEffects.THIRST, 600, 0));
                    }
                }
            }
        }));
    }

    private ModEventHandlers() {}
}

