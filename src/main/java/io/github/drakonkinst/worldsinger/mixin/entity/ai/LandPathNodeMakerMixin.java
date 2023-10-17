package io.github.drakonkinst.worldsinger.mixin.entity.ai;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.mixin.accessor.LandPathNodeMakerInvoker;
import io.github.drakonkinst.worldsinger.util.ModEnums;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LandPathNodeMaker.class)
public abstract class LandPathNodeMakerMixin extends PathNodeMaker {

    @ModifyReturnValue(method = "getCommonNodeType", at = @At("TAIL"))
    private static PathNodeType addAetherSporeSeaNodeType(PathNodeType original, BlockView world,
            BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (state.isIn(ModBlockTags.CRIMSON_SPIKE)
                || state.isIn(ModBlockTags.TALL_CRIMSON_SPINES)
                || state.isIn(ModBlockTags.AETHER_SPORE_BLOCKS)) {
            return PathNodeType.DAMAGE_CAUTIOUS;
        }

        if (state.isIn(ModBlockTags.CRIMSON_SPINES) || state.isIn(ModBlockTags.CRIMSON_SNARE)) {
            return PathNodeType.DAMAGE_OTHER;
        }

        FluidState fluidState = world.getFluidState(pos);
        if (fluidState.isIn(ModFluidTags.AETHER_SPORES)) {
            return ModEnums.PathNodeType.AETHER_SPORE_SEA;
        }

        return original;
    }

    @Inject(method = "getLandNodeType", at = @At("HEAD"), cancellable = true)
    private static void avoidSporeBlocks(BlockView world, BlockPos.Mutable pos,
            CallbackInfoReturnable<PathNodeType> cir) {
        int y = pos.getY();
        PathNodeType pathNodeType = LandPathNodeMakerInvoker.worldsinger$getCommonNodeType(world,
                pos);
        if (pathNodeType == PathNodeType.OPEN && y >= world.getBottomY() + 1) {
            PathNodeType pathNodeTypeBelow = LandPathNodeMakerInvoker.worldsinger$getCommonNodeType(
                    world, pos.down());
            if (pathNodeTypeBelow == ModEnums.PathNodeType.AETHER_SPORE_SEA) {
                cir.setReturnValue(ModEnums.PathNodeType.AETHER_SPORE_SEA);
            }
        }
    }
}
