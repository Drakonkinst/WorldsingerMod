package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlowableFluid.class)
public interface FlowableFluidInvoker {

    @Invoker("receivesFlow")
    boolean worldsinger$receivesFlow(Direction face, BlockView world, BlockPos pos,
            BlockState state, BlockPos fromPos, BlockState fromState);

    @Invoker("isMatchingAndStill")
    boolean worldsinger$isMatchingAndStill(FluidState state);
}
