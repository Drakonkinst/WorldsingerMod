package io.github.drakonkinst.worldsinger.mixin.entity.ai;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.mixin.accessor.LandPathNodeMakerInvoker;
import io.github.drakonkinst.worldsinger.util.ModEnums;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LandPathNodeMaker.class)
public abstract class LandPathNodeMakerMixin {

    @Inject(method = "getCommonNodeType", at = @At("TAIL"), cancellable = true)
    private static void addAetherSporeSeaNodeType(BlockView world, BlockPos pos,
            CallbackInfoReturnable<PathNodeType> cir) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isIn(ModBlockTags.CRIMSON_SPIKE)) {
            cir.setReturnValue(PathNodeType.DAMAGE_CAUTIOUS);
            return;
        }

        FluidState fluidState = world.getFluidState(pos);
        if (fluidState.isIn(ModFluidTags.AETHER_SPORES)) {
            cir.setReturnValue(ModEnums.PathNodeType.AETHER_SPORE_SEA);
        }
    }

    @Inject(method = "getLandNodeType", at = @At("HEAD"), cancellable = true)
    private static void avoidSporeBlocks(BlockView world, BlockPos.Mutable pos,
            CallbackInfoReturnable<PathNodeType> cir) {
        int y = pos.getY();
        PathNodeType pathNodeType = LandPathNodeMakerInvoker.worldsinger$getCommonNodeType(world,
                pos);
        if (pathNodeType == PathNodeType.OPEN && y >= world.getBottomY() + 1) {
            PathNodeType pathNodeTypeBelow = LandPathNodeMakerInvoker.worldsinger$getCommonNodeType(
                    world,
                    pos.down());
            if (pathNodeTypeBelow == ModEnums.PathNodeType.AETHER_SPORE_SEA) {
                cir.setReturnValue(ModEnums.PathNodeType.AETHER_SPORE_SEA);
            }
        }
    }
}
