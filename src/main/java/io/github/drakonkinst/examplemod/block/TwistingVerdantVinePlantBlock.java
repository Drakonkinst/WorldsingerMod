package io.github.drakonkinst.examplemod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.shape.VoxelShape;

public class TwistingVerdantVinePlantBlock extends AbstractVerticalPlantBlock {

    public static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    public TwistingVerdantVinePlantBlock(Settings settings) {
        super(settings, SHAPE);
    }

    @Override
    protected boolean canAttachTo(BlockState state, BlockState attachCandidate) {
        return TwistingVerdantVineBlock.canAttach(state, attachCandidate);
    }

    @Override
    protected Block getStem() {
        return ModBlocks.TWISTING_VERDANT_VINES;
    }
}
