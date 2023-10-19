package io.github.drakonkinst.worldsinger.fluid;

import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeGrowthSpawner;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class CrimsonSporeFluid extends LivingAetherSporeFluid {

    public CrimsonSporeFluid() {
        super(AetherSporeType.CRIMSON);
    }

    @Override
    public Fluid getStill() {
        return ModFluids.CRIMSON_SPORES;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_CRIMSON_SPORES;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.CRIMSON_SPORES_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState fluidState) {
        return ModBlocks.CRIMSON_SPORE_SEA.getDefaultState().with(Properties.LEVEL_15,
                getBlockStateLevel(fluidState));
    }

    @Override
    protected void doWaterReaction(World world, BlockPos pos, FluidState state, int sporeAmount,
            int waterAmount, Random random) {
        SporeGrowthSpawner.spawnCrimsonSporeGrowth(world, pos.toCenterPos(), sporeAmount,
                waterAmount, true, false, false);
    }

    public static class Flowing extends CrimsonSporeFluid {

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

    public static class Still extends CrimsonSporeFluid {

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
