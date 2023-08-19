package io.github.drakonkinst.examplemod.block;

import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;

public class TwistingVerdantVineBlockPlant extends AbstractVerticalPlantStemBlock {

    public static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 15.0, 12.0);

    public TwistingVerdantVineBlockPlant(Settings settings) {
        super(settings, SHAPE);
    }

    @Override
    protected Block getPlant() {
        return ModBlocks.TWISTING_VERDANT_VINES_PLANT;
    }
}
