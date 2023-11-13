package io.github.drakonkinst.worldsinger.util;

public final class ColorUtil {

    private static final float MAX_COLOR_VALUE = 255.0f;

    public static float getNormalizedRed(int color) {
        int red = (color >> 16) & 0xFF;
        return red / MAX_COLOR_VALUE;
    }

    public static float getNormalizedGreen(int color) {
        int green = (color >> 8) & 0xFF;
        return green / MAX_COLOR_VALUE;
    }

    public static float getNormalizedBlue(int color) {
        int blue = color & 0xFF;
        return blue / MAX_COLOR_VALUE;
    }

    private ColorUtil() {}
}
