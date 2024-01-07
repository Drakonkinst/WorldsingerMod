package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.github.drakonkinst.worldsinger.entity.CameraPossessable;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public interface PossessionComponent extends AutoSyncedComponent {

    // This method may only work server-side and return null otherwise
    @Nullable CameraPossessable getPossessedEntity();

    @Nullable UUID getPossessedEntityUuid();

    void setPossessedEntity(CameraPossessable entity);

    void resetPossessedEntity();

    default boolean isPossessing() {
        return getPossessedEntityUuid() != null;
    }
}
