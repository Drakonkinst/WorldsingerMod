package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;

public interface SeetheComponent extends ServerTickingComponent, AutoSyncedComponent {

    void startSeethe();

    void startSeethe(int ticks);

    void stopSeethe();

    void stopSeethe(int ticks);

    boolean isSeething();

    int getTicksUntilNextCycle();
}
