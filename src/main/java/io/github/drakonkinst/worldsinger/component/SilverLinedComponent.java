package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.github.drakonkinst.worldsinger.util.SilverLined;

public interface SilverLinedComponent extends AutoSyncedComponent, SilverLined {

    default int getSilverDurability() {
        return worldsinger$getSilverDurability();
    }

    default int getMaxSilverDurability() {
        return worldsinger$getMaxSilverDurability();
    }

    default void setSilverDurability(int durability) {
        worldsinger$setSilverDurability(durability);
    }

    @Override
    @Deprecated
    int worldsinger$getSilverDurability();

    @Override
    @Deprecated
    int worldsinger$getMaxSilverDurability();

    @Override
    @Deprecated
    void worldsinger$setSilverDurability(int durability);
}
