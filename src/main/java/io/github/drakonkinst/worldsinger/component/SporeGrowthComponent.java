package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.util.math.BlockPos;

public interface SporeGrowthComponent extends ServerTickingComponent {

    void setWater(int water);

    void setSpores(int spores);

    void addStage(int stageIncrement);

    void setOrigin(BlockPos pos);

    int getWater();

    int getSpores();

    int getStage();

    short getAge();

    boolean isInitialGrowth();

    BlockPos getOrigin();
}
