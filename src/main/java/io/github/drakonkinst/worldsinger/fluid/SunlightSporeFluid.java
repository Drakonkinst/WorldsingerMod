package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class SunlightSporeFluid extends LivingAetherSporeFluid {

    public SunlightSporeFluid() {
        super(AetherSporeType.SUNLIGHT);
    }

    @Override
    public Fluid getStill() {
        return ModFluids.SUNLIGHT_SPORES;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_SUNLIGHT_SPORES;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.SUNLIGHT_SPORES_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return ModBlocks.SUNLIGHT_SPORE_SEA.getDefaultState().with(Properties.LEVEL_15,
                getBlockStateLevel(fluidState));
    }

    @Override
    protected void doWaterReaction(World world, BlockPos pos, FluidState state, int sporeAmount,
            int waterAmount, Random random) {
        // TODO: Create light and heat
        // TODO: Should spread to multiple adjacent sunlight blocks
        world.setBlockState(pos, ModBlocks.SUNLIGHT.getDefaultState());
    }

    public static class Flowing extends SunlightSporeFluid {

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

    public static class Still extends SunlightSporeFluid {

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
