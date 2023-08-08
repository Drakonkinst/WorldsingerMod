package io.github.drakonkinst.examplemod.world;

import net.minecraft.world.World;

public final class LumarSeetheManager {

    private LumarSeetheManager() {
    }

    private static long ticksUntilNextStilling;

    public static void tick() {
        // TODO
    }

    public static boolean areSporesFluidized(World world) {
        return !world.isRaining();
    }
}
