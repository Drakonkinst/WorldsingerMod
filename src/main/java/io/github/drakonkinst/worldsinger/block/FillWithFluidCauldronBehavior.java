package io.github.drakonkinst.worldsinger.block;

import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FillWithFluidCauldronBehavior implements CauldronBehavior {

    private final Supplier<Block> cauldronBlock;

    public FillWithFluidCauldronBehavior(Supplier<Block> cauldronBlock) {
        this.cauldronBlock = cauldronBlock;
    }

    @Override
    public ActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player,
            Hand hand, ItemStack stack) {
        return CauldronBehavior.fillCauldron(world, pos, player, hand, stack,
                cauldronBlock.get().getDefaultState().with(
                        LeveledCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY);
    }
}
