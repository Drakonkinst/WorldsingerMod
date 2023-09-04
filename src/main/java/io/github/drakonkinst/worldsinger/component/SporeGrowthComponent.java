package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;

public interface SporeGrowthComponent extends ServerTickingComponent {

    void setWater(int water);

    void setSpores(int spores);

    void addStage(short stageIncrement, short maxStage);

    int getWater();

    int getSpores();

    short getStage();

    short getAge();

    boolean isInitialGrowth();
}
