package io.github.drakonkinst.worldsinger.mixin.worldgen;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.worldgen.dimension.LumarChunkGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.carver.CarverConfig;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.chunk.AquiferSampler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Carver.class)
public abstract class CarverMixin<C extends CarverConfig> {

    @Nullable
    @ModifyReturnValue(method = "getState", at = @At("RETURN"))
    private BlockState modifySporeSeaState(@Nullable BlockState state, CarverContext context,
            C config, BlockPos pos, AquiferSampler sampler) {
        if (state != null && state.isOf(LumarChunkGenerator.PLACEHOLDER_BLOCK)) {
            return LumarChunkGenerator.getSporeSeaBlockAtPos(state, context.getNoiseConfig(),
                    pos.getX(), pos.getY(), pos.getZ());
        }
        return state;
    }
}
