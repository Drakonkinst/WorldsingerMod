package io.github.drakonkinst.worldsinger.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.Nullable;

public class SteelAnvilBlock extends AnvilBlock {

    // Unused Codec
    public static final MapCodec<SteelAnvilBlock> CODEC = AbstractBlock.createCodec(
            SteelAnvilBlock::new);

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

    public SteelAnvilBlock(Settings settings) {
        super(settings);
    }

    // @Override
    // public MapCodec<AnvilBlock> getCodec() {
    //     return CODEC;
    // }
}
