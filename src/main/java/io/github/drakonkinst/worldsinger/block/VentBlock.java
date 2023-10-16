package io.github.drakonkinst.worldsinger.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.MagmaBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VentBlock extends MagmaBlock {

    // Unused Codec
    public static final MapCodec<VentBlock> CODEC = createCodec(VentBlock::new);

    public VentBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        // Do nothing
    }
}
