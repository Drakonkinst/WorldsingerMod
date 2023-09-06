package io.github.drakonkinst.worldsinger.util;

import net.minecraft.util.math.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModConstants {

    public static final String MOD_ID = "worldsinger";
    public static final String COMMON_ID = "c";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final int SECONDS_TO_TICKS = 20;
    public static final float TICKS_TO_SECONDS = 1.0f / 20.0f;
    public static final Direction[] CARDINAL_DIRECTIONS = Direction.values();

    private ModConstants() {}
}
