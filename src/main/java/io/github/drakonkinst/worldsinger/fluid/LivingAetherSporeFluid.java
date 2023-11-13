package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.world.WaterReactionManager;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeethe;
import io.github.drakonkinst.worldsinger.world.lumar.SporeKillingManager;
import io.github.drakonkinst.worldsinger.world.lumar.SporeType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class LivingAetherSporeFluid extends AetherSporeFluid implements
        WaterReactiveFluid {

    private static final int NUM_RANDOM_SPREAD_PER_RANDOM_TICK = 2;
    public static final int CATALYZE_VALUE_STILL = 250;
    public static final int CATALYZE_VALUE_FLOWING = 25;

    public LivingAetherSporeFluid(SporeType sporeType) {
        super(sporeType);
    }

    protected abstract void doWaterReaction(World world, BlockPos pos, int sporeAmount,
            int waterAmount, Random random);

    @Override
    protected boolean hasRandomTicks() {
        return true;
    }

    @Override
    protected void onRandomTick(World world, BlockPos pos, FluidState state, Random random) {
        super.onRandomTick(world, pos, state, random);

        if (world.hasRain(pos.up())) {
            this.reactToWater(world, pos, state, Integer.MAX_VALUE, random);
        }

        if (!LumarSeethe.areSporesFluidized(world)) {
            return;
        }

        // Spread to nearby dead spore sea blocks, regenerating them
        BlockState blockState = this.toBlockState(state);
        for (int i = 0; i < NUM_RANDOM_SPREAD_PER_RANDOM_TICK; ++i) {
            int offsetX = random.nextInt(3) - 1;
            int offsetY = random.nextInt(3) - 1;
            int offsetZ = random.nextInt(3) - 1;

            BlockPos blockPos = pos.add(offsetX, offsetY, offsetZ);
            if (world.getBlockState(blockPos).isOf(ModBlocks.DEAD_SPORE_SEA)
                    && world.getFluidState(blockPos).isStill()
                    && !SporeKillingManager.isSporeKillingBlockNearby(world, blockPos)) {
                world.setBlockState(blockPos, blockState);
            }
        }
    }

    @Override
    protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction,
            FluidState fluidState) {
        if (direction == Direction.DOWN) {
            FluidState neighborState = world.getFluidState(pos);
            if (neighborState.isIn(FluidTags.WATER)) {
                if (state.getBlock() instanceof FluidBlock && world instanceof World realWorld) {
                    WaterReactionManager.catalyzeAroundWater(realWorld, pos);
                    world.setBlockState(pos, ModBlocks.VERDANT_VINE_BLOCK.getDefaultState().with(
                            ModProperties.CATALYZED, true), Block.NOTIFY_ALL);
                    return;
                }
            }
        }
        super.flow(world, pos, state, direction, fluidState);
    }

    @Override
    public boolean reactToWater(World world, BlockPos pos, FluidState fluidState, int waterAmount,
            Random random) {
        // Water reaction
        int sporeAmount = this.isStill(fluidState) ? CATALYZE_VALUE_STILL : CATALYZE_VALUE_FLOWING;
        this.doWaterReaction(world, pos, sporeAmount, waterAmount, random);

        // Remove the spores
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block instanceof FluidDrainable fluidDrainable) {
            ItemStack itemStack = fluidDrainable.tryDrainFluid(null, world, pos, blockState);
            if (itemStack.isEmpty() && block instanceof FluidBlock) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
        return true;
    }
}
