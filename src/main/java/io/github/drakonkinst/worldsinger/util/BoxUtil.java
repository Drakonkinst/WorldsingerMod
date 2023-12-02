package io.github.drakonkinst.worldsinger.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class BoxUtil {

    public static Box createBoxAroundBlock(BlockPos pos, double radius) {
        double minX = pos.getX() - radius;
        double minY = pos.getY() - radius;
        double minZ = pos.getZ() - radius;
        double maxX = pos.getX() + 1.0 + radius;
        double maxY = pos.getY() + 1.0 + radius;
        double maxZ = pos.getZ() + 1.0 + radius;
        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static Box createBoxAroundPos(Vec3d pos, double radius) {
        return BoxUtil.createBoxAroundPos(pos.getX(), pos.getY(), pos.getZ(), radius);
    }

    public static Box createBoxAroundPos(double x, double y, double z, double radius) {
        double minX = x - radius;
        double minY = y - radius;
        double minZ = z - radius;
        double maxX = x + radius;
        double maxY = y + radius;
        double maxZ = z + radius;
        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private BoxUtil() {}
}
