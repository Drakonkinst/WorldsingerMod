package io.github.drakonkinst.worldsinger.mixin.item;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.block.AetherSporeBlock;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.cosmere.lumar.AetherSpores;
import io.github.drakonkinst.worldsinger.fluid.AetherSporeFluid;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlassBottleItem.class)
public abstract class GlassBottleItemMixin extends Item {

    @Shadow
    protected abstract ItemStack fill(ItemStack stack, PlayerEntity player,
            ItemStack outputStack);

    public GlassBottleItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"), cancellable = true)
    private void fillSporeBottles(World world, PlayerEntity user, Hand hand,
            CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = GlassBottleItem.raycast(world, user,
                RaycastContext.FluidHandling.SOURCE_ONLY);

        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos blockPos = blockHitResult.getBlockPos();
        FluidState fluidState = world.getFluidState(blockPos);
        if (fluidState.isIn(ModFluidTags.AETHER_SPORES)) {
            if (fluidState.getFluid() instanceof AetherSporeFluid aetherSporeFluid) {
                this.fillWithSporeBottle(world, user, itemStack, blockPos,
                        aetherSporeFluid.getSporeType(), cir);
            } else {
                Worldsinger.LOGGER.error(
                        "Expected aether spore fluid to extend AetherSporeFluid class");
            }
            return;
        }

        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isIn(ModBlockTags.AETHER_SPORE_BLOCKS)) {
            if (blockState.getBlock() instanceof AetherSporeBlock aetherSporeBlock) {
                this.fillWithSporeBottle(world, user, itemStack, blockPos,
                        aetherSporeBlock.getSporeType(), cir);
            } else {
                Worldsinger.LOGGER.error(
                        "Expected aether spore block to extend AetherSporeBlock class");
            }
        }
    }

    @Unique
    private void fillWithSporeBottle(World world, PlayerEntity user, ItemStack itemStack,
            BlockPos blockPos, AetherSpores sporeType,
            CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        world.playSound(user, user.getX(), user.getY(), user.getZ(),
                ModSoundEvents.ITEM_BOTTLE_FILL_AETHER_SPORE,
                SoundCategory.NEUTRAL, 1.0f, 1.0f);
        world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);
        cir.setReturnValue(TypedActionResult.success(
                this.fill(itemStack, user,
                        sporeType.getBottledItem().getDefaultStack()),
                world.isClient()));
    }
}
