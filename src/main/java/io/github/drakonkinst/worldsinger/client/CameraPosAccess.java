package io.github.drakonkinst.worldsinger.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;

@Environment(EnvType.CLIENT)
public interface CameraPosAccess {

    FluidState worldsinger$getSubmersedFluidState();

    BlockState worldsinger$getBlockState();
}
