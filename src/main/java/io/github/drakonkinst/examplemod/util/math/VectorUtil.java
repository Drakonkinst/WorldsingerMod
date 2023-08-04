package io.github.drakonkinst.examplemod.util.math;

import net.minecraft.util.math.Vec3d;

public final class VectorUtil {
    public static Vec3d directionTowards(Vec3d from, Vec3d to) {
        Vec3d dir = new Vec3d(to.x - from.x, to.y - from.y, to.z - from.z);
        double magnitude = dir.length();
        return new Vec3d(dir.x / magnitude, dir.y / magnitude, dir.z / magnitude);
    }

    public static Vec3d pointInDirection(Vec3d from, Vec3d to, double distance) {
        Vec3d direction = directionTowards(from, to);
        return new Vec3d(from.x + direction.x * distance, from.y + direction.y * distance, from.z + direction.z * distance);
    }
}
