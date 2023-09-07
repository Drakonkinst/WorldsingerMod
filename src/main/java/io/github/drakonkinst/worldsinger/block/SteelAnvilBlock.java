package io.github.drakonkinst.worldsinger.block;

import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.Nullable;

public class SteelAnvilBlock extends AnvilBlock {

    public SteelAnvilBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    public static BlockState getLandingState(BlockState fallingState) {
        if (fallingState.isOf(ModBlocks.STEEL_ANVIL)) {
            return ModBlocks.CHIPPED_STEEL_ANVIL.getDefaultState()
                    .with(FACING, fallingState.get(FACING));
        }
        if (fallingState.isOf(ModBlocks.CHIPPED_STEEL_ANVIL)) {
            return ModBlocks.DAMAGED_STEEL_ANVIL.getDefaultState()
                    .with(FACING, fallingState.get(FACING));
        }
        return null;
    }
}
