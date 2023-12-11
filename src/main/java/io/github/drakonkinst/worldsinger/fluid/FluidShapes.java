package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.util.VoxelShapeUtil;
import net.minecraft.util.shape.VoxelShape;

public final class FluidShapes {

    // Levels 1-8, and 0 for none
    private static final int FLUID_LEVEL_MAX = 9;
    public static final VoxelShape[] VOXEL_SHAPES = FluidShapes.initVoxelShapes();

    private static VoxelShape[] initVoxelShapes() {
        VoxelShape[] voxelShapes = new VoxelShape[FLUID_LEVEL_MAX];
        for (int i = 0; i < FLUID_LEVEL_MAX; ++i) {
            double fluidHeight = i * VoxelShapeUtil.MAX_VOXEL / FLUID_LEVEL_MAX;
            voxelShapes[i] = VoxelShapeUtil.createUpwardsCuboid(0.0, 0.0, fluidHeight);
        }
        return voxelShapes;
    }

    private FluidShapes() {}
}
