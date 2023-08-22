package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class DeadSporeFluid extends AetherSporeFluid {

    public static final int COLOR = 0x888888;
    public static final int PARTICLE_COLOR = 0xaaaaaa;

    public DeadSporeFluid() {
        super(COLOR, PARTICLE_COLOR);
    }

    @Override
    public Fluid getStill() {
        return ModFluids.DEAD_SPORES;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_DEAD_SPORES;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.DEAD_SPORES_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return ModBlocks.DEAD_SPORE_SEA_BLOCK.getDefaultState().with(Properties.LEVEL_15,
                getBlockStateLevel(fluidState));
    }

    public static class Flowing extends DeadSporeFluid {

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

    public static class Still extends DeadSporeFluid {

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