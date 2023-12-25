package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;

public interface MidnightAetherBondComponent extends AutoSyncedComponent, ServerTickingComponent {

    void updateBond(int id);

    void removeBond(int id);

    void onDeath();
    
    int getBondCount();

    default boolean hasAnyBonds() {
        return getBondCount() > 0;
    }
}
