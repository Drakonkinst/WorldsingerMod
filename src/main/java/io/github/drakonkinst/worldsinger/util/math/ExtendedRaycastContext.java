package io.github.drakonkinst.worldsinger.util.math;

import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

public class ExtendedRaycastContext extends RaycastContext {

    private final ExtendedFluidHandling extendedFluidHandling;

    public ExtendedRaycastContext(Vec3d start, Vec3d end, ShapeType shapeType,
            ExtendedFluidHandling fluidHandling, Entity entity) {
        super(start, end, shapeType, FluidHandling.NONE, entity);
        this.extendedFluidHandling = fluidHandling;
    }

    @Override
    public VoxelShape getFluidShape(FluidState state, BlockView world, BlockPos pos) {
        return extendedFluidHandling.handled(state) ? state.getShape(world, pos)
                : VoxelShapes.empty();
    }

    public enum ExtendedFluidHandling {
        // Also includes Sunlight blocks for gameplay cohesion
        // Also includes flowing blocks, since this is only currently used for splash potions
        SPORE_SEA(state -> state.isIn(ModFluidTags.AETHER_SPORES_OR_SUNLIGHT));

        private final Predicate<FluidState> predicate;

        ExtendedFluidHandling(Predicate<FluidState> predicate) {
            this.predicate = predicate;
        }

        public boolean handled(FluidState state) {
            return this.predicate.test(state);
        }
    }
}
