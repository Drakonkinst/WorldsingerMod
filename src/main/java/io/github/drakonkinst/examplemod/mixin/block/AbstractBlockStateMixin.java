package io.github.drakonkinst.examplemod.mixin.block;

import io.github.drakonkinst.examplemod.fluid.FluidShapes;
import io.github.drakonkinst.examplemod.fluid.Fluidlogged;
import io.github.drakonkinst.examplemod.mixin.accessor.AbstractBlockAccessor;
import io.github.drakonkinst.examplemod.mixin.accessor.AbstractBlockSettingsAccessor;
import java.util.function.ToIntFunction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {

    @Shadow
    protected abstract BlockState asBlockState();

    @Shadow
    public abstract Block getBlock();

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target =
            "Ljava/util/function/ToIntFunction;applyAsInt" +
                    "(Ljava/lang/Object;)I"))
    private <T> int injectLuminance(ToIntFunction<T> instance, T t) {
        if (t instanceof BlockState state && state.getEntries() != null && state.contains(
                Fluidlogged.PROPERTY_FLUID)) {
            Fluid fluid = Fluidlogged.getFluid(state);
            FluidBlock fluidBlock = Fluidlogged.getFluidBlockForFluid(fluid);
            if (fluidBlock != null) {
                // Apply luminance from the fluid block to the block itself
                // TODO: What happens if the original block has luminance too?
                AbstractBlock.Settings settings = ((AbstractBlockAccessor) fluidBlock).examplemod$getSettings();
                return ((AbstractBlockSettingsAccessor) settings).examplemod$getLuminance()
                        .applyAsInt(state);
            }
        }
        return instance.applyAsInt(t);
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
    private void makeCustomFluidTickable(Direction direction, BlockState neighborState,
            WorldAccess world,
            BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        Fluid fluid = Fluidlogged.getFluid(this.asBlockState());
        boolean noFluid = (fluid == null) || Fluids.EMPTY.equals(fluid);
        if (!noFluid) {
            world.scheduleFluidTick(pos, fluid, fluid.getTickRate(world));
        }
    }

    // Credit: https://github.com/apace100/water-walking-fix
    @Inject(method =
            "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;" +
                    "Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("RETURN"),
            cancellable = true)
    private void fixFluidloggedCollisionShape(BlockView world, BlockPos pos, ShapeContext context,
            CallbackInfoReturnable<VoxelShape> cir) {
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

    @Inject(method = "getFluidState", at = @At("RETURN"), cancellable = true)
    private void examplemod$supportMultipleFluidsInState(CallbackInfoReturnable<FluidState> cir) {
        BlockState state = this.asBlockState();
        boolean isVanillaWaterlogged =
                state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED);
        if (!isVanillaWaterlogged && state.contains(Fluidlogged.PROPERTY_FLUID)) {
            Fluid fluid = Fluidlogged.getFluid(state);
            if (fluid != null) {
                cir.setReturnValue(fluid.getDefaultState());
            }
        }
    }

    // Tossing these in for now, haven't given them a proper look yet
    @Redirect(method =
            "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;" +
                    "Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/Block;getCollisionShape(Lnet/minecraft/block/BlockState;"
                    +
                    "Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;" +
                    "Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape injectCustomFluidCollisionShape(Block instance, BlockState state,
            BlockView world, BlockPos pos
            , ShapeContext context) {
        return instance.getCollisionShape(
                state.contains(Fluidlogged.PROPERTY_FLUID)
                        ? state.with(Fluidlogged.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }

    @Redirect(method =
            "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;" +
                    "Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/block/Block;getOutlineShape(Lnet/minecraft/block/BlockState;" +
                    "Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;" +
                    "Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape injectCustomFluidOutlineShape(Block instance, BlockState state,
            BlockView world, BlockPos pos,
            ShapeContext context) {
        return instance.getOutlineShape(
                state.contains(Fluidlogged.PROPERTY_FLUID)
                        ? state.with(Fluidlogged.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }

    @Redirect(method = "getSidesShape", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/block/Block;getSidesShape" +
                    "(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)"
                    +
                    "Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape injectCustomFluidSidesShape(Block instance, BlockState state,
            BlockView world,
            BlockPos pos) {
        ShapeContext context = ShapeContext.absent();
        return instance.getCollisionShape(
                state.contains(Fluidlogged.PROPERTY_FLUID)
                        ? state.with(Fluidlogged.PROPERTY_FLUID, 0)
                        : state,
                world, pos, context
        );
    }
}