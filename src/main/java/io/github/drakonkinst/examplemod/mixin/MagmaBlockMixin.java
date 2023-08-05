package io.github.drakonkinst.examplemod.mixin;

import io.github.drakonkinst.examplemod.block.AetherSporeFluidBlock;
import io.github.drakonkinst.examplemod.block.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.MagmaBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MagmaBlock.class)
public abstract class MagmaBlockMixin {
    @Inject(method = "scheduledTick", at = @At("RETURN"))
    private void addSporeFluidizationCheckScheduledTick(BlockState state, ServerWorld world, BlockPos pos,
                                                        Random random, CallbackInfo ci) {
        AetherSporeFluidBlock.update(world, pos.up(), world.getBlockState(pos.up()), state);
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"))
    private void addSporeFluidizationCheckNeighborUpdate(BlockState state, Direction direction,
                                                         BlockState neighborState, WorldAccess world, BlockPos pos,
                                                         BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (direction == Direction.UP && neighborState.isIn(ModBlockTags.AETHER_SPORE_BLOCKS)) {
            world.scheduleBlockTick(pos, (MagmaBlock) (Object) this, 20);
        }
    }
}
