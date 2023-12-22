package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.entity.data.SilverLinedEntityData;
import net.minecraft.entity.vehicle.BoatEntity;

public class SilverLinedBoatEntityData extends SilverLinedEntityData {

    public static final int MAX_DURABILITY = 2500;

    public SilverLinedBoatEntityData(BoatEntity boatEntity) {
        super(boatEntity);
    }

    @Override
    public int getMaxSilverDurability() {
        return MAX_DURABILITY;
    }
}
