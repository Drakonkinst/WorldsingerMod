package io.github.drakonkinst.examplemod.fluid;

import io.github.drakonkinst.examplemod.block.ModBlocks;
import io.github.drakonkinst.examplemod.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class VerdantSporeFluid extends AetherSporeFluid {

    private static final float FOG_RED = 0.0f;
    private static final float FOG_GREEN = 1.0f;
    private static final float FOG_BLUE = 0.0f;

    public VerdantSporeFluid() {
        super(FOG_RED, FOG_GREEN, FOG_BLUE);
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
