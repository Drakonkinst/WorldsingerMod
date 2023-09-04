package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.component.SporeGrowthComponent;
import net.minecraft.nbt.NbtCompound;

public class SporeGrowthEntityData implements SporeGrowthComponent {

    private static final String NBT_KEY_WATER_REMAINING = "WaterRemaining";
    private static final String NBT_KEY_SPORES_REMAINING = "SporesRemaining";
    private static final String NBT_KEY_STAGE = "Stage";
    private static final String NBT_KEY_AGE = "Age";
    private static final String NBT_KEY_INITIAL_GROWTH = "InitialGrowth";

    private final AbstractSporeGrowthEntity entity;
    private int waterRemaining;
    private int sporesRemaining;
    private short stage = 0;
    private short age = 0;
    private boolean initial;

    public SporeGrowthEntityData(AbstractSporeGrowthEntity entity) {
        this.entity = entity;
    }

    @Override
    public void setWater(int water) {
        this.waterRemaining = water;
    }

    @Override
    public void setSpores(int spores) {
        this.sporesRemaining = spores;
    }

    @Override
    public void addStage(short stageIncrement, short maxStage) {
        this.stage = (short) (this.stage + stageIncrement);
        if (this.stage > maxStage) {
            this.stage %= maxStage;
        }
    }

    @Override
    public int getWater() {
        return waterRemaining;
    }

    @Override
    public int getSpores() {
        return sporesRemaining;
    }

    @Override
    public short getStage() {
        return stage;
    }

    @Override
    public short getAge() {
        return age;
    }

    @Override
    public boolean isInitialGrowth() {
        return initial;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.waterRemaining = tag.getInt(NBT_KEY_WATER_REMAINING);
        this.sporesRemaining = tag.getInt(NBT_KEY_SPORES_REMAINING);
        this.initial = tag.getBoolean(NBT_KEY_INITIAL_GROWTH);
        this.stage = tag.getShort(NBT_KEY_STAGE);
        this.age = tag.getShort(NBT_KEY_AGE);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt(NBT_KEY_WATER_REMAINING, this.waterRemaining);
        tag.putInt(NBT_KEY_SPORES_REMAINING, this.sporesRemaining);
        tag.putBoolean(NBT_KEY_INITIAL_GROWTH, this.initial);
        tag.putShort(NBT_KEY_STAGE, this.stage);
        tag.putShort(NBT_KEY_AGE, this.age);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public void serverTick() {
        ++age;
    }

    public AbstractSporeGrowthEntity getEntity() {
        return entity;
    }
}
