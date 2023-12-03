package io.github.drakonkinst.worldsinger.cosmere.lumar;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.component.SporeGrowthComponent;
import io.github.drakonkinst.worldsinger.entity.SporeGrowthEntity;
import io.github.drakonkinst.worldsinger.util.BlockPosUtil;
import io.github.drakonkinst.worldsinger.util.BoxUtil;
import io.github.drakonkinst.worldsinger.util.math.Int3;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class GrowableAetherSpores<T extends SporeGrowthEntity> extends AetherSpores {

    private static final double COMBINE_GROWTH_MAX_RADIUS = 3.0;
    private final Class<T> sporeGrowthEntityTypeClass;

    public GrowableAetherSpores(Class<T> sporeGrowthEntityTypeClass) {
        this.sporeGrowthEntityTypeClass = sporeGrowthEntityTypeClass;
    }

    @Override
    public void doReaction(World world, Vec3d pos, int spores, int water, Random random) {
        this.spawnSporeGrowth(world, pos, spores, water, true, false, false, Int3.ZERO);
    }

    public void spawnSporeGrowth(World world, Vec3d pos, int spores, int water,
            boolean initialGrowth, boolean isSmall, boolean isSplit, Int3 lastDir) {
        // If one already exists nearby, just augment that one
        if (!isSplit && this.tryCombineWithNearbyGrowth(world, pos, spores, water, initialGrowth,
                isSmall)) {
            return;
        }

        T entity = this.getSporeGrowthEntityType().create(world);
        if (entity == null) {
            return;
        }
        entity.setPosition(pos);
        entity.setSporeData(spores, water, initialGrowth);
        entity.setLastDir(lastDir);
        if (isSmall) {
            entity.setInitialStage(this.getSmallStage());
        }

        world.spawnEntity(entity);
    }

    private boolean tryCombineWithNearbyGrowth(World world, Vec3d pos, int spores, int water,
            boolean isInitial, boolean isSmall) {
        Box box = BoxUtil.createBoxAroundPos(pos, COMBINE_GROWTH_MAX_RADIUS);
        List<T> nearbySporeGrowthEntities = world.getEntitiesByClass(sporeGrowthEntityTypeClass,
                box, sporeGrowthEntity -> {
                    SporeGrowthComponent sporeGrowthData = sporeGrowthEntity.getSporeGrowthData();
                    return sporeGrowthData.getAge() == 0
                            && sporeGrowthData.isInitialGrowth() == isInitial
                            && (sporeGrowthData.getStage() == 1) == isSmall;
                });
        if (nearbySporeGrowthEntities.isEmpty()) {
            return false;
        }
        T existingSporeGrowthEntity = nearbySporeGrowthEntities.get(0);
        SporeGrowthComponent sporeGrowthData = existingSporeGrowthEntity.getSporeGrowthData();
        sporeGrowthData.setSpores(sporeGrowthData.getSpores() + spores);
        sporeGrowthData.setWater(sporeGrowthData.getWater() + water);
        return true;
    }

    public abstract EntityType<T> getSporeGrowthEntityType();

    public abstract int getSmallStage();

    @Override
    public void doReactionFromFluidContainer(World world, BlockPos fluidContainerPos, int spores,
            int water, Random random) {
        super.doReactionFromFluidContainer(world, fluidContainerPos.up(), spores, water, random);
    }

    @Override
    public void doReactionFromSplashBottle(World world, Vec3d pos, int spores, int water,
            Random random, boolean affectingFluidContainer) {
        if (affectingFluidContainer) {
            BlockPos posAbove = BlockPosUtil.toBlockPos(pos).up();
            BlockState stateAbove = world.getBlockState(posAbove);
            if (stateAbove.isIn(ModBlockTags.SPORES_CAN_GROW) || stateAbove.isIn(
                    ModBlockTags.SPORES_CAN_BREAK)) {
                this.spawnSporeGrowth(world, pos.add(0.0, 1.0, 0.0), spores, water, true, true,
                        false, Int3.ZERO);
            }
        }
        this.spawnSporeGrowth(world, pos, spores, water, true, true, false, Int3.ZERO);
    }
}
