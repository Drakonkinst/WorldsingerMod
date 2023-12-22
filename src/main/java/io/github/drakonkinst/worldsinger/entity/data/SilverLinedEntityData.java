package io.github.drakonkinst.worldsinger.entity.data;

import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.SilverLinedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.nbt.NbtCompound;

public abstract class SilverLinedEntityData implements SilverLinedComponent {

    private static final String NBT_KEY = "SilverDurability";

    private final Entity entity;
    private int silverDurability;

    public SilverLinedEntityData(BoatEntity boatEntity) {
        this.entity = boatEntity;
    }

    @Override
    public void setSilverDurability(int durability) {
        this.silverDurability = Math.max(0, Math.min(durability, this.getMaxSilverDurability()));
        ModComponents.SILVER_LINED.sync(entity);
    }

    @Override
    public int getSilverDurability() {
        return silverDurability;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.silverDurability = tag.getInt(NBT_KEY);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(NBT_KEY, this.silverDurability);
    }

    public Entity getEntity() {
        return entity;
    }
}
