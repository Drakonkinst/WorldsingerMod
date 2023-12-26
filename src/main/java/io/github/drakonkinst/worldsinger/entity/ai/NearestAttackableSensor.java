package io.github.drakonkinst.worldsinger.entity.ai;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.registry.SBLSensors;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

// GenericAttackTargetSensor doesn't properly support custom predicates, so we're making our own
public class NearestAttackableSensor<E extends LivingEntity> extends
        PredicateSensor<LivingEntity, E> {

    @Override
    protected void sense(ServerWorld level, E entity) {
        BrainUtils.setMemory(entity, MemoryModuleType.NEAREST_ATTACKABLE, testForEntity(entity));
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return Collections.singletonList(MemoryModuleType.NEAREST_ATTACKABLE);
    }

    protected LivingEntity testForEntity(E entity) {
        LivingTargetCache matcher = BrainUtils.getMemory(entity, MemoryModuleType.VISIBLE_MOBS);

        if (matcher == null) {
            return null;
        }

        return findMatches(entity, matcher);
    }

    @Nullable
    protected LivingEntity findMatches(E entity, LivingTargetCache matcher) {
        return matcher.findFirst(target -> predicate().test(target, entity)).orElse(null);
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return SBLSensors.GENERIC_ATTACK_TARGET.get();
    }
}
