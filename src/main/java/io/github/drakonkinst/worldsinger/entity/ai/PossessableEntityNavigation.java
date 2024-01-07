package io.github.drakonkinst.worldsinger.entity.ai;

import io.github.drakonkinst.worldsinger.entity.CameraPossessable;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class PossessableEntityNavigation<E extends MobEntity & CameraPossessable> extends
        MobNavigation {

    private final E castEntity;

    public PossessableEntityNavigation(E entity, World world) {
        super(entity, world);
        this.castEntity = entity;
    }

    @Override
    public void tick() {
        if (!castEntity.isBeingPossessed()) {
            super.tick();
        }
    }
}
