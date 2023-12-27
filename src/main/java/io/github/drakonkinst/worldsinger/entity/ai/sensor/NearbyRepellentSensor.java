package io.github.drakonkinst.worldsinger.entity.ai.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor;
import net.tslat.smartbrainlib.util.BrainUtils;

public class NearbyRepellentSensor<E extends LivingEntity> extends PredicateSensor<BlockState, E> {

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            MemoryModuleType.NEAREST_REPELLENT);

    private int horizontalRange = 8;
    private int verticalRange = 4;

    public NearbyRepellentSensor<E> setHorizontalRange(int horizontalRange) {
        this.horizontalRange = horizontalRange;
        return this;
    }

    public NearbyRepellentSensor<E> setVerticalRange(int verticalRange) {
        this.verticalRange = verticalRange;
        return this;
    }

    @Override
    public NearbyRepellentSensor<E> setPredicate(BiPredicate<BlockState, E> predicate) {
        super.setPredicate(predicate);
        return this;
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends ExtendedSensor<?>> type() {
        return ModSensors.NEARBY_REPELLENT_SENSOR.get();
    }

    @Override
    protected void sense(ServerWorld world, E entity) {
        Brain<?> brain = entity.getBrain();
        BrainUtils.setMemory(brain, MemoryModuleType.NEAREST_REPELLENT,
                BlockPos.findClosest(entity.getBlockPos(), horizontalRange, verticalRange,
                        pos -> predicate().test(world.getBlockState(pos), entity)).orElse(null));
    }
}
