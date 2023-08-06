package io.github.drakonkinst.examplemod.weather;

import net.minecraft.world.World;

public class LumarSeetheManager {

    public static boolean areSporesFluidized(World world) {
        return !world.isRaining();
    }
}
