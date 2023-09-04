package io.github.drakonkinst.worldsinger.util.math;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Direction;

public record Int3(int x, int y, int z) {

    public static List<Int3> DIAGONAL_3D = Int3.generateDiagonal3d();
    public static List<Int3> CARDINAL_3D = Int3.generateCardinal3d();
    public static Int3 ZERO = new Int3(0, 0, 0);
    private static Direction[] CARDINAL_DIRECTIONS = Direction.values();

    private static List<Int3> generateDiagonal3d() {
        List<Int3> list = new ArrayList<>(3 * 3 * 3);
        for (int xOffset = -1; xOffset <= 1; ++xOffset) {
            for (int yOffset = -1; yOffset <= 1; ++yOffset) {
                for (int zOffset = -1; zOffset <= 1; ++zOffset) {
                    list.add(new Int3(xOffset, yOffset, zOffset));
                }
            }
        }
        return list;
    }

    private static List<Int3> generateCardinal3d() {
        List<Int3> list = new ArrayList<>(7);
        for (int xOffset = -1; xOffset <= 1; ++xOffset) {
            for (int yOffset = -1; yOffset <= 1; ++yOffset) {
                for (int zOffset = -1; zOffset <= 1; ++zOffset) {
                    int delta = Math.abs(xOffset) + Math.abs(yOffset) + Math.abs(zOffset);
                    if (delta > 1) {
                        continue;
                    }
                    list.add(new Int3(xOffset, yOffset, zOffset));
                }
            }
        }
        return list;
    }

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public Direction toDirection(Direction defaultValue) {
        for (Direction direction : CARDINAL_DIRECTIONS) {
            if (direction.getOffsetX() == x && direction.getOffsetY() == y
                    && direction.getOffsetZ() == z) {
                return direction;
            }
        }
        return defaultValue;
    }
}
