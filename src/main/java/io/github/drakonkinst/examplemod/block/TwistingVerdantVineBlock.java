package io.github.drakonkinst.examplemod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.shape.VoxelShape;

public class TwistingVerdantVineBlock extends AbstractVerticalPlantStemBlock {

    public static boolean canAttach(BlockState state, BlockState attachCandidate) {
        if (attachCandidate.isOf(ModBlocks.VERDANT_VINE_BRANCH)) {
            return true;
        }
        if (attachCandidate.isOf(ModBlocks.VERDANT_VINE_SNARE)) {
            return VerdantVineSnareBlock.getDirection(attachCandidate) == getGrowthDirection(state);
        }
        return false;
    }

    public static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 15.0, 12.0);

    public TwistingVerdantVineBlock(Settings settings) {
        super(settings, SHAPE);
    }

    @Override
    protected boolean canAttachTo(BlockState state, BlockState attachCandidate) {
        return canAttach(state, attachCandidate);
    }

    @Override
    protected Block getPlant() {
        return ModBlocks.TWISTING_VERDANT_VINES_PLANT;
    }
}
