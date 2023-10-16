package io.github.drakonkinst.worldsinger.mixin.accessor;

import com.mojang.serialization.Codec;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FluidBlock.class)
public interface FluidBlockAccessor {

    @Accessor("FLUID_CODEC")
    static Codec<FlowableFluid> worldsinger$getFluidCodec() {
        throw new UnsupportedOperationException();
    }
}
