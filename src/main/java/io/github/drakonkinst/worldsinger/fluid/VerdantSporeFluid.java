package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.cosmere.lumar.VerdantSpores;
import io.github.drakonkinst.worldsinger.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class VerdantSporeFluid extends LivingAetherSporeFluid {

    public VerdantSporeFluid() {
        super(VerdantSpores.getInstance());
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
    public Type getReactiveType() {
        return Type.VERDANT_SPORES;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return ModBlocks.VERDANT_SPORE_SEA.getDefaultState()
                .with(Properties.LEVEL_15, FlowableFluid.getBlockStateLevel(fluidState));
    }

    public static class Flowing extends VerdantSporeFluid {

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

    public static class Still extends VerdantSporeFluid {

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
