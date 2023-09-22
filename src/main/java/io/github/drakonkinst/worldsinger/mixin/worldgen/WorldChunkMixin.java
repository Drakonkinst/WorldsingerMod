package io.github.drakonkinst.worldsinger.mixin.worldgen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.worldgen.dimension.LumarChunkGenerator;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {

    // Run a block tick on all generated spore sea blocks at sea level to check for spore-killing
    // blocks, creating the dead spore ring around saltstone islands
    @WrapOperation(method = "runPostProcessing", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;onScheduledTick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"))
    private void addDeadSporePostProcessing(FluidState instance, World world, BlockPos pos,
            Operation<Void> original) {
        if (pos.getY() == LumarChunkGenerator.SEA_LEVEL - 1 && instance.isIn(
                ModFluidTags.AETHER_SPORES)) {
            world.scheduleBlockTick(pos, world.getBlockState(pos).getBlock(), 1);
        }
        original.call(instance, world, pos);
    }
}
