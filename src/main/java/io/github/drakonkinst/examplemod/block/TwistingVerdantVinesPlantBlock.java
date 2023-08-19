package io.github.drakonkinst.examplemod.block;

import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;

public class TwistingVerdantVinesPlantBlock extends AbstractVerticalPlantBlock {

    public static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    public TwistingVerdantVinesPlantBlock(Settings settings) {
        super(settings, SHAPE);
    }

    @Override
    protected Block getStem() {
        return ModBlocks.TWISTING_VERDANT_VINES;
    }
}
