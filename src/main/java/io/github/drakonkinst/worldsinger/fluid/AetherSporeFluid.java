package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.block.AetherSporeFluidBlock;
import io.github.drakonkinst.worldsinger.world.LumarSeetheManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class AetherSporeFluid extends FlowableFluid {

    private static final float MAX_COLOR_VALUE = 255.0f;

    public static final float FOG_START = 0.25f;
    public static final float FOG_END = 3.0f;
    public static final float HORIZONTAL_DRAG_MULTIPLIER = 0.7f;
    public static final float VERTICAL_DRAG_MULTIPLIER = 0.8f;
    public static final float DAMAGE = 10.0f;

    // How fast this fluid pushes entities.
    // Water uses the value 0.014, and lava uses 0.007 in the Nether and 0.0023 otherwise
    public static final double FLUID_SPEED = 0.012;

    public static float getNormalizedRed(int color) {
        int red = (color >> 16) & 0xFF;
        return red / MAX_COLOR_VALUE;
    }

    public static float getNormalizedGreen(int color) {
        int green = (color >> 8) & 0xFF;
        return green / MAX_COLOR_VALUE;
    }

    public static float getNormalizedBlue(int color) {
        int blue = color & 0xFF;
        return blue / MAX_COLOR_VALUE;
    }

    private final int color;
    private final int particleColor;
    private final float fogRed;
    private final float fogGreen;
    private final float fogBlue;

    public AetherSporeFluid(int color, int particleColor) {
        super();
        this.color = color;
        this.particleColor = particleColor;
        this.fogRed = AetherSporeFluid.getNormalizedRed(color);
        this.fogGreen = AetherSporeFluid.getNormalizedGreen(color);
        this.fogBlue = AetherSporeFluid.getNormalizedBlue(color);
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
    protected void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
        if (!LumarSeetheManager.areSporesFluidized(world)) {
            return;
        }
        BlockPos blockPos = pos.up();
        if (world.getBlockState(blockPos).isAir() && !world.getBlockState(blockPos)
                .isOpaqueFullCube(world, blockPos)) {
            if (random.nextInt(100) == 0) {
                double spawnX = (double) pos.getX() + random.nextDouble();
                double spawnY = (double) pos.getY() + 1.0;
                double spawnZ = (double) pos.getZ() + random.nextDouble();
                world.addParticle(ParticleTypes.SPLASH, spawnX, spawnY, spawnZ, 0.0, 1.0,
                        0.0);
                // world.playSound(spawnX, spawnY, spawnZ, SoundEvents.BLOCK_LAVA_POP,
                //         SoundCategory.BLOCKS,
                //         0.2f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.15f, false);
            }
            if (random.nextInt(200) == 0) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT,
                        SoundCategory.BLOCKS, 0.2f + random.nextFloat() * 0.2f,
                        0.9f + random.nextFloat() * 0.15f, false);
            }
        }
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

    public int getColor() {
        return color;
    }
}
