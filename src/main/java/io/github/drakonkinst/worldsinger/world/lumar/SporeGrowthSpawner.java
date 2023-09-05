package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.entity.VerdantSporeGrowthEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class SporeGrowthSpawner {

    public static void spawnVerdantSporeGrowth(World world, Vec3d pos, int spores, int water,
            boolean initialGrowth, boolean isSmall) {
        VerdantSporeGrowthEntity entity = ModEntityTypes.VERDANT_SPORE_GROWTH.create(world);
        if (entity == null) {
            return;
        }
        entity.setPosition(pos);
        entity.setSporeData(spores, water, initialGrowth);
        if (isSmall) {
            entity.setInitialStage(VerdantSporeGrowthEntity.MAX_STAGE);
        }
        world.spawnEntity(entity);
    }

    private SporeGrowthSpawner() {}
}
