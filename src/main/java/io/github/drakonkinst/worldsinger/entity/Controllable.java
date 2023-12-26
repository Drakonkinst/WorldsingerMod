package io.github.drakonkinst.worldsinger.entity;

import java.util.UUID;

// Currently, only Player Controllers are currently supported
// UUID lookups for other entities are expensive
public interface Controllable {

    void setControllerUuid(UUID uuid);

    UUID getControllerUuid();
}
