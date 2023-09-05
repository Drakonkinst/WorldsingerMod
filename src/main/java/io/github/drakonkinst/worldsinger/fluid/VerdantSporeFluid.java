package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeGrowthSpawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class VerdantSporeFluid extends LivingAetherSporeFluid {

    public static final int CATALYZE_VALUE_STILL = 250;
    public static final int CATALYZE_VALUE_FLOWING = 25;

    public VerdantSporeFluid() {
        super(AetherSporeType.VERDANT);
    }

    @Override
    public Fluid getStill() {
        return ModFluids.VERDANT_SPORES;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_VERDANT_SPORES;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.VERDANT_SPORES_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return ModBlocks.VERDANT_SPORE_SEA_BLOCK.getDefaultState().with(Properties.LEVEL_15,
                getBlockStateLevel(fluidState));
    }

    @Override
    public void reactToWater(World world, BlockPos pos, FluidState fluidState, int waterAmount,
            Random random) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        int catalyzeValue = this.isStill(fluidState) ? CATALYZE_VALUE_STILL
                : CATALYZE_VALUE_FLOWING;

        if (block instanceof FluidDrainable fluidDrainable) {
            ItemStack itemStack = fluidDrainable.tryDrainFluid(world, pos, blockState);
            if (itemStack.isEmpty() && block instanceof FluidBlock) {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }

        SporeGrowthSpawner.spawnVerdantSporeGrowth(world, pos.toCenterPos(), catalyzeValue,
                waterAmount, true, false);
    }

    public static class Flowing extends VerdantSporeFluid {

        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }
    }

    public static class Still extends VerdantSporeFluid {

        @Override
        public int getLevel(FluidState fluidState) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}
