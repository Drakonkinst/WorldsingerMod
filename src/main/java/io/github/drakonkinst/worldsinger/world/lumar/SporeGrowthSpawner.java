package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.entity.VerdantSporeGrowthEntity;
import net.minecraft.world.World;

public final class SporeGrowthSpawner {

    public static void spawnVerdantSporeGrowth(World world, int spores, int water,
            boolean initialGrowth, boolean isSmall) {
        VerdantSporeGrowthEntity entity = ModEntityTypes.VERDANT_SPORE_GROWTH.create(world);
        if (entity == null) {
            return;
        }
        entity.setSporeData(spores, water, initialGrowth);
        if (isSmall) {
            entity.setInitialStage(VerdantSporeGrowthEntity.MAX_STAGE);
        }
        world.spawnEntity(entity);
    }

    private SporeGrowthSpawner() {}
}
