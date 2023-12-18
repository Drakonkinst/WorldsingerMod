package io.github.drakonkinst.worldsinger.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ThirstManagerComponent extends AutoSyncedComponent, ServerTickingComponent {

    // Call when consuming an item
    void drink(Item item, ItemStack stack);

    // Add exhaustion like hunger, which can lead to water loss
    void addExhaustion(float exhaustion);

    // Directly add water
    void addWater(int water);

    // Directly remove water
    void removeWater(int water);

    int getWaterLevel();
}
