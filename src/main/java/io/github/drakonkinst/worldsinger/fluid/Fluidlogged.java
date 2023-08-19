package io.github.drakonkinst.worldsinger.fluid;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.BigDripleafBlock;
import net.minecraft.block.BigDripleafStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ConduitBlock;
import net.minecraft.block.CoralParentBlock;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.GlowLichenBlock;
import net.minecraft.block.HangingRootsBlock;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LightBlock;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.MangroveRootsBlock;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.PropaguleBlock;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.SculkVeinBlock;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SmallDripleafBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

// Manages making blocks fluidloggable by fluids other than water.
public final class Fluidlogged {

    private Fluidlogged() {
    }

    public static final List<Class<? extends Block>> VANILLA_WATERLOGGABLES = getVanillaWaterloggables();
    public static final List<Identifier> WATERLOGGABLE_FLUIDS = List.of(
            new Identifier("minecraft", "water"),
            new Identifier("minecraft", "lava"),
            new Identifier("worldsinger", "verdant_spores"));
    public static final FluidProperty PROPERTY_FLUID = FluidProperty.of("fluidlogged");

    private static final Map<Fluid, FluidBlock> fluidToFluidBlocks = new HashMap<>();

    public static void initialize() {
    }

    public static Fluid getFluid(BlockState state) {
        if (state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED)) {
            return Fluids.WATER;
        }
        if (!state.contains(Fluidlogged.PROPERTY_FLUID)) {
            return null;
        }
        int index = state.get(Fluidlogged.PROPERTY_FLUID) - 1;
        if (index < 0) {
            return Fluids.EMPTY;
        }
        if (index >= Fluidlogged.WATERLOGGABLE_FLUIDS.size()) {
            return null;
        }
        Identifier id = Fluidlogged.WATERLOGGABLE_FLUIDS.get(index);
        if (id == null) {
            return null;
        }
        return Registries.FLUID.get(id);
    }

    public static void registerFluidBlockForFluid(Fluid fluid, FluidBlock fluidBlock) {
        fluidToFluidBlocks.put(fluid, fluidBlock);
    }

    public static FluidBlock getFluidBlockForFluid(Fluid fluid) {
        return fluidToFluidBlocks.get(fluid);
    }

    public static int getFluidIndex(Fluid fluid) {
        if (fluid.equals(Fluids.EMPTY)) {
            return 0;
        }
        return Fluidlogged.WATERLOGGABLE_FLUIDS.indexOf(Registries.FLUID.getId(fluid)) + 1;
    }

    public static boolean isVanillaWaterloggable(Object block) {
        Class<?> blockClass = block.getClass();
        for (Class<? extends Block> waterloggableBlockClass : VANILLA_WATERLOGGABLES) {
            if (waterloggableBlockClass.isAssignableFrom(blockClass)) {
                return true;
            }
        }
        return false;
    }

    // https://minecraft.fandom.com/wiki/Waterlogging
    // Any vanilla block class that implements Waterloggable
    private static List<Class<? extends Block>> getVanillaWaterloggables() {
        return Arrays.asList(
                AbstractRailBlock.class,
                AbstractSignBlock.class,
                AmethystClusterBlock.class,
                // BarrierBlock.class, // Not until 1.20.2
                BigDripleafBlock.class,
                BigDripleafStemBlock.class,
                CampfireBlock.class,
                CandleBlock.class,
                ChainBlock.class,
                ChestBlock.class,
                ConduitBlock.class,
                CoralParentBlock.class,
                DecoratedPotBlock.class,
                EnderChestBlock.class,
                FenceBlock.class,
                GlowLichenBlock.class,
                HangingRootsBlock.class,
                HangingSignBlock.class,
                HorizontalConnectingBlock.class,
                LadderBlock.class,
                LanternBlock.class,
                LeavesBlock.class,
                LightBlock.class,
                LightningRodBlock.class,
                MangroveRootsBlock.class,
                PointedDripstoneBlock.class,
                PropaguleBlock.class,
                ScaffoldingBlock.class,
                SculkSensorBlock.class,
                SculkShriekerBlock.class,
                SculkVeinBlock.class,
                SeaPickleBlock.class,
                SlabBlock.class,
                SmallDripleafBlock.class,
                StairsBlock.class,
                TrapdoorBlock.class,
                WallBlock.class

        );
    }
}
