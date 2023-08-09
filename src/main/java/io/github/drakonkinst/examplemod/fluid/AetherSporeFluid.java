package io.github.drakonkinst.examplemod.fluid;

import io.github.drakonkinst.examplemod.block.AetherSporeFluidBlock;
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
    public static final float HORIZONTAL_DRAG_MULTIPLIER = 0.7f;
    public static final float VERTICAL_DRAG_MULTIPLIER = 0.8f;
    public static final float DAMAGE = 10.0f;

    // How fast this fluid pushes entities.
    // Water uses the value 0.014, and lava uses 0.007 in the Nether and 0.0023 otherwise
    public static final double FLUID_SPEED = 0.012;

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
    public void onScheduledTick(World world, BlockPos pos, FluidState state) {
        super.onScheduledTick(world, pos, state);
        if (this.isStill(state) && !AetherSporeFluidBlock.shouldFluidize(
                world.getBlockState(pos.down()))) {
            AetherSporeFluidBlock.updateFluidization(world, pos, state.getBlockState(), false);
        }
    }

    @Override
    protected boolean canBeReplacedWith(FluidState fluidState, BlockView blockView,
            BlockPos blockPos,
            Fluid fluid,
            Direction direction) {
        return false;
    }

    @Override
    // This is used when the fluid "pathfinds" to see which way it should flow
    // Since it only flows as far as lava in the Overworld, match the flow speed
    protected int getFlowSpeed(WorldView worldView) {
        return 2;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView worldView) {
        return 2;
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
