package io.github.drakonkinst.worldsinger.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

// Called on server-side only, along with the entity_hurt_player advancement criterion.
// Contains more information than ServerLivingEntityEvents.ALLOW_DAMAGE, but cannot cancel
// the damage event.
public interface ServerPlayerHurtCallback {

    Event<ServerPlayerHurtCallback> EVENT = EventFactory.createArrayBacked(
            ServerPlayerHurtCallback.class,
            (listeners) -> (player, source, damageDealt, damageTaken, wasBlocked) -> {
                for (ServerPlayerHurtCallback listener : listeners) {
                    listener.onHurt(player, source, damageDealt, damageTaken, wasBlocked);
                }
            });

    void onHurt(PlayerEntity player, DamageSource source, float damageDealt, float damageTaken,
            boolean wasBlocked);
}
