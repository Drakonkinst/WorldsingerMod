package io.github.drakonkinst.examplemod.mixin.accessor;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface BlockAccessor {
    @Invoker("appendProperties")
    void examplemod$invokeAppendProperties(StateManager.Builder<Block, BlockState> builder);
}
