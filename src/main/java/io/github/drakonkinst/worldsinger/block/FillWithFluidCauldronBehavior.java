package io.github.drakonkinst.worldsinger.block;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FillWithFluidCauldronBehavior implements CauldronBehavior {

    protected final SoundEvent fillCauldronSound;
    private final Supplier<Block> cauldronBlock;

    public FillWithFluidCauldronBehavior(Supplier<Block> cauldronBlock,
            SoundEvent fillCauldronSound) {
        this.cauldronBlock = Suppliers.memoize(cauldronBlock::get);
        this.fillCauldronSound = fillCauldronSound;
    }

    @Override
    public ItemActionResult interact(BlockState state, World world, BlockPos pos,
            PlayerEntity player, Hand hand, ItemStack stack) {
        return CauldronBehavior.fillCauldron(world, pos, player, hand, stack,
                cauldronBlock.get().getDefaultState().with(LeveledCauldronBlock.LEVEL, 3),
                fillCauldronSound);
    }
}
