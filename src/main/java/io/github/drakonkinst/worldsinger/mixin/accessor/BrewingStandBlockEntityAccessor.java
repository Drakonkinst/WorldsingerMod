package io.github.drakonkinst.worldsinger.mixin.accessor;

import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandBlockEntity.class)
public interface BrewingStandBlockEntityAccessor {

    @Accessor("inventory")
    DefaultedList<ItemStack> worldsinger$getInventory();

    @Accessor("fuel")
    int worldsinger$getFuel();

    @Accessor("fuel")
    @Mutable
    void worldsinger$setFuel(int fuel);
}
