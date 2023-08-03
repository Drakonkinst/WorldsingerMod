package io.github.drakonkinst.examplemod.mixin;

import io.github.drakonkinst.examplemod.FluidShapes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Credit: https://github.com/apace100/water-walking-fix

@Mixin(AbstractBlock.AbstractBlockState.class)
public class FluidloggedWalkMixin {

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("RETURN"), cancellable = true)
    private void examplemod$fixFluidloggedCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        FluidState fluidState = world.getFluidState(pos);

        int level = fluidState.getLevel();
        if (level == 0) {
            return;
        }

        VoxelShape shape = FluidShapes.VOXEL_SHAPES[level];
        VoxelShape shapeBelow = FluidShapes.VOXEL_SHAPES[level - 1];

        if (context.isAbove(shapeBelow, pos, true)
                && context.canWalkOnFluid(world.getFluidState(pos.up()), fluidState)) {
            VoxelShape original = cir.getReturnValue();
            cir.setReturnValue(VoxelShapes.union(original, shape));
        }

    }
}