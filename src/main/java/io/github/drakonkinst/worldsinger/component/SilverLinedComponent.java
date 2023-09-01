package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

public interface SilverLinedComponent extends AutoSyncedComponent {

    int getSilverDurability();

    int getMaxSilverDurability();

    void setSilverDurability(int durability);
}
