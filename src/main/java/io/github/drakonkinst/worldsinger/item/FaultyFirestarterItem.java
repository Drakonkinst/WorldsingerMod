package io.github.drakonkinst.worldsinger.item;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FaultyFirestarterItem extends FlintAndSteelItem {

    private final float successChance;

    public FaultyFirestarterItem(float successChance, Settings settings) {
        super(settings);
        this.successChance = successChance;
    }

    private boolean isValidUse(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (CampfireBlock.canBeLit(state)
                || CandleBlock.canBeLit(state)
                || CandleCakeBlock.canBeLit(state)) {
            return true;
        }
        BlockPos sidePos = pos.offset(context.getSide());
        return AbstractFireBlock.canPlaceAt(world, sidePos, context.getHorizontalPlayerFacing());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (this.isValidUse(context)) {
            // Only run on server side to prevent client-server desync from random chance
            if (!world.isClient()) {
                boolean successful = world.getRandom().nextFloat() < successChance;
                if (successful) {
                    // Play sound for player as well since this is no longer running on client
                    world.playSound(null, context.getBlockPos(),
                            SoundEvents.ITEM_FLINTANDSTEEL_USE,
                            SoundCategory.BLOCKS, 1.0f,
                            world.getRandom().nextFloat() * 0.4f + 0.8f);
                    return super.useOnBlock(context);
                } else {
                    // Play sound at different pitch
                    world.playSound(null, context.getBlockPos(),
                            SoundEvents.ITEM_FLINTANDSTEEL_USE,
                            SoundCategory.BLOCKS, 1.0f,
                            world.getRandom().nextFloat() * 0.4f + 1.5f);
                }
            }
            return ActionResult.success(world.isClient());
        }
        return ActionResult.FAIL;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity,
            Hand hand) {
        return super.useOnEntity(stack, user, entity, hand);
    }
}
