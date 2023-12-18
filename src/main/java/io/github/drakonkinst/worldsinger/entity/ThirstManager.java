package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.datatables.DataTableRegistry;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.ThirstManagerComponent;
import io.github.drakonkinst.worldsinger.registry.ModDataTables;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

// Similar to Hunger, but uses "thirst" and "hydration" instead of "hunger" and "saturation".
// Thirst is simpler and foods only have a "water" amount, not a hydration multiplier.
// Thirst goes down the same regardless of difficulty, including Peaceful.
// Over-hydration does not do anything, though water will last longer overall.
public class ThirstManager implements ThirstManagerComponent {

    private static final String KEY_WATER_LEVEL = "WaterLevel";
    private static final String KEY_HYDRATION_LEVEL = "WaterHydrationLevel";
    private static final String KEY_EXHAUSTION_LEVEL = "WaterExhaustionLevel";
    private static final int MAX_WATER_LEVEL = 20;
    private static final int MIN_WATER_THRESHOLD = 6;
    private static final float INITIAL_HYDRATION = 5.0f;
    private static final float HYDRATION_MULTIPLIER = 2.0f;
    private static final float EXHAUSTION_PER_WATER_LEVEL = 4.0f;
    private static final float MAX_EXHAUSTION = 4.0f;

    private final LivingEntity entity;
    private int waterLevel = MAX_WATER_LEVEL;
    private float hydrationLevel = INITIAL_HYDRATION;
    private float exhaustion;

    public ThirstManager(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void serverTick() {
        if (exhaustion >= EXHAUSTION_PER_WATER_LEVEL) {
            exhaustion -= EXHAUSTION_PER_WATER_LEVEL;
            if (hydrationLevel > 0.0f) {
                hydrationLevel = Math.max(hydrationLevel - 1.0f, 0.0f);
                ModComponents.THIRST_MANAGER.sync(entity);
            } else {
                removeWater(1);
                // Sync is already called in this method
            }
        }
        // TODO: Bad effects and damage if running low
        // TODO: Stop at a certain threshold
    }

    @Override
    public void addWater(int water) {
        waterLevel = Math.min(waterLevel + water, MAX_WATER_LEVEL);
        hydrationLevel = Math.min(hydrationLevel + water * HYDRATION_MULTIPLIER, waterLevel);
        ModComponents.THIRST_MANAGER.sync(entity);
    }

    @Override
    public void removeWater(int water) {
        waterLevel = Math.max(0, waterLevel - water);
        hydrationLevel = Math.min(waterLevel, hydrationLevel);
        ModComponents.THIRST_MANAGER.sync(entity);
    }

    @Override
    public void addExhaustion(float exhaustion) {
        this.exhaustion = Math.min(this.exhaustion + exhaustion, MAX_EXHAUSTION);
    }

    public void drink(Item item, ItemStack stack) {
        int water = DataTableRegistry.INSTANCE.get(ModDataTables.CONSUMABLE_HYDRATION)
                .getIntForItem(item);
        if (water == 0) {
            return;
        }

        if (water > 0) {
            addWater(water);
        } else {
            removeWater(water);
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        waterLevel = nbt.getInt(KEY_WATER_LEVEL);
        hydrationLevel = nbt.getFloat(KEY_HYDRATION_LEVEL);
        exhaustion = nbt.getFloat(KEY_EXHAUSTION_LEVEL);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putInt(KEY_WATER_LEVEL, waterLevel);
        nbt.putFloat(KEY_HYDRATION_LEVEL, hydrationLevel);
        nbt.putFloat(KEY_EXHAUSTION_LEVEL, exhaustion);
    }

    @Override
    public int getWaterLevel() {
        return waterLevel;
    }
}
