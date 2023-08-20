package io.github.drakonkinst.worldsinger.mixin.block;

import io.github.drakonkinst.worldsinger.Constants;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Waterloggable.class)
public interface WaterloggableMixin {

    // TODO: Might be able to replace these with INJECTS

    /**
     * @author Drakonkinst
     * @reason Allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        if (state.contains(ModProperties.FLUIDLOGGABLE)) {
            return state.get(ModProperties.FLUIDLOGGABLE) == 0
                    && !state.get(Properties.WATERLOGGED)
                    && (fluid.equals(Fluids.WATER) ||
                    Fluidlogged.WATERLOGGABLE_FLUIDS.contains(Registries.FLUID.getId(fluid)));
        } else {
            return !state.get(Properties.WATERLOGGED) && (fluid.equals(Fluids.WATER));
        }
    }

    /**
     * @author Drakonkinst
     * @reason Allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state,
            FluidState fluidState) {
        Fluid fluid = fluidState.getFluid();
        if (state.contains(ModProperties.FLUIDLOGGABLE) && !state.get(Properties.WATERLOGGED) &&
                state.get(ModProperties.FLUIDLOGGABLE) == 0) {
            if (!world.isClient()) {
                BlockState newState = state;
                if (fluid.equals(Fluids.WATER)) {
                    newState = newState.with(Properties.WATERLOGGED, true);
                }
                int index = Fluidlogged.getFluidIndex(fluid);
                if (index == -1) {
                    Constants.LOGGER.warn("Tried to fill a block with a not loggable fluid!");
                    return false;
                }
                world.setBlockState(pos, newState.with(ModProperties.FLUIDLOGGABLE, index),
                        Block.NOTIFY_ALL);
                world.scheduleFluidTick(pos, fluid, fluid.getTickRate(world));
            }
            return true;
        } else if (!state.get(Properties.WATERLOGGED)) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(Properties.WATERLOGGED, true),
                        Block.NOTIFY_ALL);
                world.scheduleFluidTick(pos, fluid, fluid.getTickRate(world));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * @author Drakonkinst
     * @reason Allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        if (state.get(Properties.WATERLOGGED) ||
                (state.contains(ModProperties.FLUIDLOGGABLE)
                        && state.get(ModProperties.FLUIDLOGGABLE) > 0)) {
            Fluid fluid = Fluidlogged.getFluid(state);
            if (state.get(Properties.WATERLOGGED)) {
                fluid = Fluids.WATER;
            }
            if (state.contains(ModProperties.FLUIDLOGGABLE)) {
                state = state.with(ModProperties.FLUIDLOGGABLE, 0);
            }
            world.setBlockState(pos, state.with(Properties.WATERLOGGED, false), Block.NOTIFY_ALL);
            if (!state.canPlaceAt(world, pos)) {
                world.breakBlock(pos, true);
            }
            if (fluid == null) {
                return ItemStack.EMPTY;
            }
            return new ItemStack(fluid.getBucketItem());
        }
        return ItemStack.EMPTY;
    }

    /**
     * @author Drakonkinst
     * @reason Allow waterloggable blocks to be loggable with any fluid
     */
    @Overwrite
    default Optional<SoundEvent> getBucketFillSound() {
        return Optional.empty();
    }
}
