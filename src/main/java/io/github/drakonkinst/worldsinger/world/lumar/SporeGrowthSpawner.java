package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.entity.VerdantSporeGrowthEntity;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class SporeGrowthSpawner {

    public static void spawnVerdantSporeGrowth(World world, Vec3d pos, int spores, int water,
            boolean initialGrowth, boolean isSmall) {
        SporeGrowthSpawner.spawnVerdantSporeGrowth(world, pos, spores, water, initialGrowth,
                isSmall, Int3.ZERO);
    }

    public static void spawnVerdantSporeGrowth(World world, Vec3d pos, int spores, int water,
            boolean initialGrowth, boolean isSmall, Int3 lastDir) {
        VerdantSporeGrowthEntity entity = ModEntityTypes.VERDANT_SPORE_GROWTH.create(world);
        if (entity == null) {
            return;
        }
        entity.setPosition(pos);
        entity.setSporeData(spores, water, initialGrowth);
        if (!lastDir.isZero()) {
            entity.setLastDir(lastDir);
        }
        if (isSmall) {
            entity.setInitialStage(VerdantSporeGrowthEntity.MAX_STAGE);
        }
        world.spawnEntity(entity);
    }

    private SporeGrowthSpawner() {}
}
