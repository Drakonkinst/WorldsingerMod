package io.github.drakonkinst.worldsinger.util.math;

import java.util.ArrayList;
import java.util.List;

public record Int3(int x, int y, int z) {

    public static List<Int3> DIAGONAL_3D = Int3.generateDiagonal3d();
    public static Int3 ZERO = new Int3(0, 0, 0);

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
}
