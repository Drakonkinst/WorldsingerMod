package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.component.SporeGrowthComponent;
import io.github.drakonkinst.worldsinger.entity.CrimsonSporeGrowthEntity;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypes;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import java.util.List;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

// Spawns crimson growth entities
public final class CrimsonSporeManager {

    public static void spawnCrimsonSporeGrowth(World world, Vec3d pos, int spores, int water,
            boolean initialGrowth, boolean isSmall, boolean isSplit) {
        CrimsonSporeManager.spawnCrimsonSporeGrowth(world, pos, spores, water, initialGrowth,
                isSmall, isSplit, Int3.ZERO);
    }

    public static void spawnCrimsonSporeGrowth(World world, Vec3d pos, int spores, int water,
            boolean initialGrowth, boolean isSmall, boolean isSplit, Int3 lastDir) {
        // If one already exists nearby, just augment that one
        if (!isSplit && CrimsonSporeManager.checkForNearbyNewCrimsonGrowth(world, pos, spores,
                water,
                initialGrowth, isSmall)) {
            return;
        }

        CrimsonSporeGrowthEntity entity = ModEntityTypes.CRIMSON_SPORE_GROWTH.create(world);
        if (entity == null) {
            return;
        }
        entity.setPosition(pos);
        entity.setSporeData(spores, water, initialGrowth);
        entity.setLastDir(lastDir);
        if (isSmall) {
            entity.setInitialStage(CrimsonSporeGrowthEntity.MAX_STAGE - 1);
        }

        world.spawnEntity(entity);
    }

    private static boolean checkForNearbyNewCrimsonGrowth(World world, Vec3d pos, int spores,
            int water, boolean initialGrowth, boolean isSmall) {
        Box box = Box.from(pos).expand(VerdantSporeManager.SAME_GROWTH_SEARCH_RADIUS);
        List<CrimsonSporeGrowthEntity> nearbySporeGrowthEntities = world
                .getEntitiesByClass(CrimsonSporeGrowthEntity.class, box, sporeGrowthEntity -> {
                    SporeGrowthComponent sporeGrowthData = sporeGrowthEntity.getSporeGrowthData();
                    return sporeGrowthData.getAge() == 0
                            && sporeGrowthData.isInitialGrowth() == initialGrowth
                            && (sporeGrowthData.getStage() == 1) == isSmall;
                });
        if (nearbySporeGrowthEntities.isEmpty()) {
            return false;
        }
        CrimsonSporeGrowthEntity existingSporeGrowthEntity = nearbySporeGrowthEntities.get(0);
        SporeGrowthComponent sporeGrowthData = existingSporeGrowthEntity.getSporeGrowthData();
        sporeGrowthData.setSpores(sporeGrowthData.getSpores() + spores);
        sporeGrowthData.setWater(sporeGrowthData.getWater() + water);
        return true;
    }

    private CrimsonSporeManager() {}
}
