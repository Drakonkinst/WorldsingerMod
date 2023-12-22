package io.github.drakonkinst.worldsinger.mixin.compat.smartbrainlib;

import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.object.SquareRadius;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(NearbyLivingEntitySensor.class)
public abstract class NearbyLivingEntitySensorPatchMixin<E extends LivingEntity> extends
        PredicateSensor<LivingEntity, E> {

    @Shadow
    @Nullable
    protected SquareRadius radius;

    /**
     * @author Drakonkinst
     * @reason Temporary fix to patch to 1.20.5 snapshots
     */
    @Overwrite
    protected void sense(ServerWorld level, E entity) {
        SquareRadius radius = this.radius;

        if (radius == null) {
            double dist = entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);

            radius = new SquareRadius(dist, dist);
        }

        List<LivingEntity> entities = EntityRetrievalUtil.getEntities(level, entity.getBoundingBox()
                        .expand(radius.xzRadius(), radius.yRadius(), radius.xzRadius()),
                obj -> obj instanceof LivingEntity livingEntity && predicate().test(livingEntity,
                        entity));

        entities.sort(Comparator.comparingDouble(entity::squaredDistanceTo));

        BrainUtils.setMemory(entity, MemoryModuleType.MOBS, entities);
        BrainUtils.setMemory(entity, MemoryModuleType.VISIBLE_MOBS,
                new LivingTargetCache(entity, entities));
    }
}
