package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;

public interface SilverLinedComponent extends Component, AutoSyncedComponent {

    int getSilverDurability();

    int getMaxSilverDurability();

    void setSilverDurability(int durability);
}
