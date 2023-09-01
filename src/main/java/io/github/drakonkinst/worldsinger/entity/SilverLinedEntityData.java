package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SilverLinedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;

// TODO: Not sure if this can apply to non-entities, so making it entity-specific for now
public class SilverLinedEntityData implements SilverLinedComponent {

    public static final int MAX_BOAT_SILVER_DURABILITY = 100;

    private static final String NBT_SILVER_DURABILITY = "silverDurability";

    private final Entity entity;
    private final int maxSilverDurability;
    private int silverDurability;

    public SilverLinedEntityData(BoatEntity boatEntity) {
        this.entity = boatEntity;
        this.maxSilverDurability = MAX_BOAT_SILVER_DURABILITY;
    }

    @Override
    public int getSilverDurability() {
        return silverDurability;
    }

    @Override
    public int getMaxSilverDurability() {
        return maxSilverDurability;
    }

    @Override
    public void setSilverDurability(int durability) {
        this.silverDurability = Math.max(0, Math.min(durability, maxSilverDurability));
        ModComponents.SILVER_LINED_ENTITY.sync(entity);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.silverDurability = tag.getInt(NBT_SILVER_DURABILITY);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(NBT_SILVER_DURABILITY, this.silverDurability);
    }

    public Entity getEntity() {
        return entity;
    }
}
