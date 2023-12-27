package io.github.drakonkinst.worldsinger.entity.ai.sensor;

import java.util.function.Supplier;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.tslat.smartbrainlib.SBLConstants;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

@SuppressWarnings("UnqualifiedStaticUsage")
public final class ModSensors {

    public static final Supplier<SensorType<NearbyRepellentSensor<?>>> NEARBY_REPELLENT_SENSOR = register(
            "nearby_repellent_sensor", NearbyRepellentSensor::new);

    public static void initialize() {

    }

    private static <T extends ExtendedSensor<?>> Supplier<SensorType<T>> register(String id,
            Supplier<T> sensor) {
        return SBLConstants.SBL_LOADER.registerSensorType(id, sensor);
    }

    private ModSensors() {}
}
