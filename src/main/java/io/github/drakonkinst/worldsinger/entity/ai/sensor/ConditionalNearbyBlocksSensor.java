package io.github.drakonkinst.worldsinger.entity.ai.sensor;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.sensor.custom.NearbyBlocksSensor;

// Mod author hasn't added conditional functionality yet, so I'll do it myself
public class ConditionalNearbyBlocksSensor<E extends LivingEntity> extends NearbyBlocksSensor<E> {

    protected Predicate<E> shouldRunPredicate = entity -> true;

    public ConditionalNearbyBlocksSensor<E> shouldRun(Predicate<E> predicate) {
        this.shouldRunPredicate = predicate;
        return this;
    }

    @Override
    protected void sense(ServerWorld level, E entity) {
        if (shouldRunPredicate.test(entity)) {
            super.sense(level, entity);
        }
    }
}
