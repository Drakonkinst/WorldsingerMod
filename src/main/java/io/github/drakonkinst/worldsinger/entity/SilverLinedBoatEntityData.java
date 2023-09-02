package io.github.drakonkinst.worldsinger.entity;

import net.minecraft.entity.vehicle.BoatEntity;

public class SilverLinedBoatEntityData extends SilverLinedEntityData {

    public static final int MAX_DURABILITY = 100;

    public SilverLinedBoatEntityData(BoatEntity boatEntity) {
        super(boatEntity);
    }

    @Override
    public int getMaxSilverDurability() {
        return MAX_DURABILITY;
    }
}
