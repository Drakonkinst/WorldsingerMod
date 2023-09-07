package io.github.drakonkinst.worldsinger.mixin.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.SteelAnvilBlock;
import java.util.function.BiConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin {

    @Unique
    // Half the chance for a steel anvil to break
    private static final float STEEL_ANVIL_DAMAGE_CHANCE = 0.12f * 0.5f;

    @Inject(method = "canUse", at = @At("RETURN"), cancellable = true)
    private void allowSteelAnvil(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.isIn(ModBlockTags.STEEL_ANVIL)) {
            cir.setReturnValue(true);
        }
    }

    @WrapOperation(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V"))
    private void addSteelAnvilBehavior(ScreenHandlerContext context,
            BiConsumer<World, BlockPos> function, Operation<Void> original, PlayerEntity player,
            ItemStack stack) {
        context.run((world, pos) -> {
            BlockState state = world.getBlockState(pos);
            if (!state.isIn(ModBlockTags.STEEL_ANVIL)) {
                original.call(context, function);
                return;
            }

            if (!player.isCreative()
                    && player.getRandom().nextFloat() < STEEL_ANVIL_DAMAGE_CHANCE) {
                BlockState damagedAnvilState = SteelAnvilBlock.getLandingState(state);
                if (damagedAnvilState == null) {
                    world.removeBlock(pos, false);
                    world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0);
                } else {
                    world.setBlockState(pos, damagedAnvilState, Block.NOTIFY_LISTENERS);
                    world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
                }
            } else {
                world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
            }
        });
    }
}
