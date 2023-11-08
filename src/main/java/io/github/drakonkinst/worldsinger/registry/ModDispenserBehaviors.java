package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.item.ModItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;

public class ModDispenserBehaviors {

    private static final ItemDispenserBehavior FLUID_BUCKET_BEHAVIOR = new ItemDispenserBehavior() {
        private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

        @Override
        public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
            FluidModificationItem fluidModificationItem = (FluidModificationItem) stack.getItem();
            BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
            ServerWorld world = pointer.world();
            if (fluidModificationItem.placeFluid(null, world, blockPos, null)) {
                fluidModificationItem.onEmptied(null, world, stack, blockPos);
                return new ItemStack(Items.BUCKET);
            }
            return this.fallbackBehavior.dispense(pointer, stack);
        }
    };

    public static void register() {
        DispenserBlock.registerBehavior(ModItems.DEAD_SPORES_BUCKET, FLUID_BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(ModItems.VERDANT_SPORES_BUCKET, FLUID_BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(ModItems.CRIMSON_SPORES_BUCKET, FLUID_BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(ModItems.ZEPHYR_SPORES_BUCKET, FLUID_BUCKET_BEHAVIOR);
        DispenserBlock.registerBehavior(ModItems.SUNLIGHT_SPORES_BUCKET, FLUID_BUCKET_BEHAVIOR);
        // TODO: Add remaining 2 spore fluid behaviors
    }
}
