package io.github.drakonkinst.examplemod.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class AetherSporeFluid extends FlowableFluid {

    public static final float FOG_START = 0.25f;
    public static final float FOG_END = 3.0f;

    private final float fogRed;
    private final float fogGreen;
    private final float fogBlue;

    public AetherSporeFluid(float fogRed, float fogGreen, float fogBlue) {
        super();
        this.fogRed = fogRed;
        this.fogGreen = fogGreen;
        this.fogBlue = fogBlue;
    }

    @Override
    public int getLevel(FluidState state) {
        return state.getLevel();
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    @Override
    protected boolean isInfinite(World world) {
        return true;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockView blockView,
            BlockPos blockPos,
            Fluid fluid,
            Direction direction) {
        return false;
    }

    @Override
    protected int getFlowSpeed(WorldView worldView) {
        return 3;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView worldView) {
        return 1;
    }

    @Override
    public int getTickRate(WorldView worldView) {
        return 5;
    }

    @Override
    protected float getBlastResistance() {
        return 100.0F;
    }

    public float getFogRed() {
        return fogRed;
    }

    public float getFogGreen() {
        return fogGreen;
    }

    public float getFogBlue() {
        return fogBlue;
    }
}
