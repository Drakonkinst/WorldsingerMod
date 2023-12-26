package io.github.drakonkinst.worldsinger.entity.ai;

import io.github.drakonkinst.worldsinger.entity.Controllable;
import java.util.UUID;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowEntity;

public class FollowControllerBehavior<E extends PathAwareEntity & Controllable> extends
        FollowEntity<E, PlayerEntity> {

    protected PlayerEntity controller = null;

    public FollowControllerBehavior() {
        following(this::getController);
    }

    protected PlayerEntity getController(E entity) {
        if (controller == null) {
            UUID controllerUUID = entity.getControllerUuid();
            if (controllerUUID != null) {
                controller = entity.getWorld().getPlayerByUuid(controllerUUID);
            }
        }
        if (controller != null && controller.isRemoved()) {
            controller = null;
        }
        return controller;
    }
}
