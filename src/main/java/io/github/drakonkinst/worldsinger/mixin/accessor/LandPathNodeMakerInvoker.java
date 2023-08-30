package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LandPathNodeMaker.class)
public interface LandPathNodeMakerInvoker {

    @Invoker("getCommonNodeType")
    static PathNodeType worldsinger$getCommonNodeType(BlockView world, BlockPos pos) {
        throw new AssertionError();
    }
}
