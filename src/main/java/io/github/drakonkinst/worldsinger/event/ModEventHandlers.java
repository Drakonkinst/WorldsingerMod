package io.github.drakonkinst.worldsinger.event;

import io.github.drakonkinst.worldsinger.component.ModComponents;
import net.minecraft.entity.player.PlayerEntity;

public final class ModEventHandlers {

    public static void register() {
        FinishConsumingItemCallback.EVENT.register(((entity, stack) -> {
            if (entity instanceof PlayerEntity player) {
                ModComponents.THIRST_MANAGER.get(player).drink(stack.getItem(), stack);
            }
        }));
    }

    private ModEventHandlers() {}

    ;
}

