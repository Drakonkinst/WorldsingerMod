package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.component.SporeGrowthComponent;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.entity.VerdantSporeGrowthEntity;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import java.util.List;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// Spawns verdant growth entities
public final class SporeGrowthSpawner {

    private static final double SAME_GROWTH_SEARCH_RADIUS = 3.0;

    public static void spawnVerdantSporeGrowth(World world, Vec3d pos, int spores, int water,
            boolean initialGrowth, boolean isSmall, boolean isSplit) {
        SporeGrowthSpawner.spawnVerdantSporeGrowth(world, pos, spores, water, initialGrowth,
                isSmall, isSplit, Int3.ZERO);
    }

    public static void spawnVerdantSporeGrowth(World world, Vec3d pos, int spores, int water,
            boolean initialGrowth, boolean isSmall, boolean isSplit, Int3 lastDir) {
        // If one already exists nearby, just augment that one
        if (!isSplit && SporeGrowthSpawner.checkForNearbyNewVerdantGrowth(world, pos, spores, water,
                initialGrowth, isSmall)) {
            return;
        }

        VerdantSporeGrowthEntity entity = ModEntityTypes.VERDANT_SPORE_GROWTH.create(world);
        if (entity == null) {
            return;
        }
        entity.setPosition(pos);
        entity.setSporeData(spores, water, initialGrowth);
        entity.setLastDir(lastDir);
        if (isSmall) {
            entity.setInitialStage(VerdantSporeGrowthEntity.MAX_STAGE);
        }

        world.spawnEntity(entity);
    }

    private static boolean checkForNearbyNewVerdantGrowth(World world, Vec3d pos, int spores,
            int water, boolean initialGrowth, boolean isSmall) {
        Box box = Box.from(pos).expand(SAME_GROWTH_SEARCH_RADIUS);
        List<VerdantSporeGrowthEntity> nearbySporeGrowthEntities = world
                .getEntitiesByClass(VerdantSporeGrowthEntity.class, box, sporeGrowthEntity -> {
                    SporeGrowthComponent sporeGrowthData = sporeGrowthEntity.getSporeGrowthData();
                    return sporeGrowthData.getAge() == 0
                            && sporeGrowthData.isInitialGrowth() == initialGrowth
                            && (sporeGrowthData.getStage() == 1) == isSmall;
                });
        if (nearbySporeGrowthEntities.isEmpty()) {
            return false;
        }
        VerdantSporeGrowthEntity existingSporeGrowthEntity = nearbySporeGrowthEntities.get(0);
        SporeGrowthComponent sporeGrowthData = existingSporeGrowthEntity.getSporeGrowthData();
        sporeGrowthData.setSpores(sporeGrowthData.getSpores() + spores);
        sporeGrowthData.setWater(sporeGrowthData.getWater() + water);
        return true;
    }

    private SporeGrowthSpawner() {}
}
