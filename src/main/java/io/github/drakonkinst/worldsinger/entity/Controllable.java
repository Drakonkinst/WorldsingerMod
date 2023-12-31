package io.github.drakonkinst.worldsinger.entity;

import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;

// Currently, only Player Controllers are currently supported
// UUID lookups for other entities are more expensive (ServerWorld#getEntity)
public interface Controllable {

    void onStartControlling();
    
    void setControllerUuid(UUID uuid);

    UUID getControllerUuid();

    PlayerEntity getController();
}
