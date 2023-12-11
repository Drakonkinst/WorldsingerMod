package io.github.drakonkinst.worldsinger.util.math;

import io.github.drakonkinst.worldsinger.util.ModConstants;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Direction;

public record Int3(int x, int y, int z) {

    public static final Int3 ZERO = new Int3(0, 0, 0);
    public static final Int3 UP = new Int3(0, 1, 0);
    public static final Int3 DOWN = new Int3(0, -1, 0);
    public static final Int3 NORTH = new Int3(0, 0, -1);
    public static final Int3 SOUTH = new Int3(0, 0, 1);
    public static final Int3 WEST = new Int3(-1, 0, 0);
    public static final Int3 EAST = new Int3(1, 0, 0);
    public static final List<Int3> DIAGONAL_3D = Int3.generateDiagonal3d();
    public static final List<Int3> CARDINAL_3D = List.of(UP, DOWN, NORTH, SOUTH, EAST, WEST);

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

    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    public Direction toDirection(Direction defaultValue) {
        for (Direction direction : ModConstants.CARDINAL_DIRECTIONS) {
            if (direction.getOffsetX() == x && direction.getOffsetY() == y
                    && direction.getOffsetZ() == z) {
                return direction;
            }
        }
        return defaultValue;
    }

    public Int3 opposite() {
        return new Int3(-x, -y, -z);
    }

    // Converts all values to their sign
    public Int3 toSigns() {
        return new Int3(Integer.signum(x), Integer.signum(y), Integer.signum(z));
    }
}
