package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import io.github.drakonkinst.worldsinger.world.SilverLined;

public interface SilverLinedComponent extends AutoSyncedComponent, SilverLined {

    int getSilverDurability();

    int getMaxSilverDurability();

    void setSilverDurability(int durability);
}
