package io.github.drakonkinst.examplemod;

import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;

public class FluidShapes {
    private static final double MAX_VOXEL_WIDTH = 16.0;
    private static final int FLUID_LEVEL_MAX = 9;

    public static final VoxelShape[] VOXEL_SHAPES = initVoxelShapes();

    private static VoxelShape[] initVoxelShapes() {
        VoxelShape[] voxelShapes = new VoxelShape[FLUID_LEVEL_MAX];
        for (int i = 0; i < FLUID_LEVEL_MAX; ++i) {
            double fluidHeight = i * MAX_VOXEL_WIDTH / FLUID_LEVEL_MAX;
            voxelShapes[i] = Block.createCuboidShape(0.0, 0.0, 0.0, MAX_VOXEL_WIDTH, fluidHeight, MAX_VOXEL_WIDTH);
        }
        return voxelShapes;
    }
}
