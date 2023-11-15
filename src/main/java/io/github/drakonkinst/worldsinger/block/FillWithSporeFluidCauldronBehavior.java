package io.github.drakonkinst.worldsinger.block;

import com.google.common.base.Suppliers;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import io.github.drakonkinst.worldsinger.world.lumar.SporeKillingManager;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FillWithSporeFluidCauldronBehavior extends FillWithFluidCauldronBehavior {

    private final Supplier<Block> deadSporeCauldronBlock;

    public FillWithSporeFluidCauldronBehavior(Supplier<Block> cauldronBlock,
            Supplier<Block> deadSporeCauldronBlock) {
        super(cauldronBlock, ModSoundEvents.ITEM_BUCKET_EMPTY_AETHER_SPORE);
        this.deadSporeCauldronBlock = Suppliers.memoize(deadSporeCauldronBlock::get);
    }

    @Override
    public ActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player,
            Hand hand, ItemStack stack) {
        if (SporeKillingManager.isSporeKillingBlockNearby(world, pos)) {
            return CauldronBehavior.fillCauldron(world, pos, player, hand, stack,
                    deadSporeCauldronBlock.get().getDefaultState()
                            .with(LeveledCauldronBlock.LEVEL, 3), fillCauldronSound);
        }
        return super.interact(state, world, pos, player, hand, stack);
    }
}
