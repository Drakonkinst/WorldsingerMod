package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.cosmere.lumar.ZephyrSpores;
import io.github.drakonkinst.worldsinger.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class ZephyrSporeFluid extends LivingAetherSporeFluid {

    public ZephyrSporeFluid() {
        super(ZephyrSpores.getInstance());
    }

    @Override
    public Fluid getStill() {
        return ModFluids.ZEPHYR_SPORES;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_ZEPHYR_SPORES;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.ZEPHYR_SPORES_BUCKET;
    }

    @Override
    public Type getReactiveType() {
        return Type.ZEPHYR_SPORES;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return ModBlocks.ZEPHYR_SPORE_SEA.getDefaultState()
                .with(Properties.LEVEL_15, getBlockStateLevel(fluidState));
    }

    public static class Flowing extends ZephyrSporeFluid {

        @Override
        public int getLevel(FluidState fluidState) {
            return fluidState.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return false;
        }

        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }
    }

    public static class Still extends ZephyrSporeFluid {

        @Override
        public int getLevel(FluidState fluidState) {
            return AetherSporeFluid.MAX_LEVEL;
        }

        @Override
        public boolean isStill(FluidState fluidState) {
            return true;
        }
    }
}
