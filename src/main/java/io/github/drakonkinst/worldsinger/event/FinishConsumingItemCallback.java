package io.github.drakonkinst.worldsinger.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

// Fabric hasn't made one of these yet, so guess it's up to me
// Fired whenever an item is successfully eaten, so it cannot cancel the consumption.
public interface FinishConsumingItemCallback {

    Event<FinishConsumingItemCallback> EVENT = EventFactory.createArrayBacked(
            FinishConsumingItemCallback.class, (listeners) -> (entity, item) -> {
                for (FinishConsumingItemCallback listener : listeners) {
                    listener.onConsume(entity, item);
                }
            });

    void onConsume(LivingEntity entity, ItemStack stack);
}
