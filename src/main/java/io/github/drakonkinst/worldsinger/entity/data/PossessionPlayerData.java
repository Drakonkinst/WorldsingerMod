package io.github.drakonkinst.worldsinger.entity.data;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.PossessionComponent;
import io.github.drakonkinst.worldsinger.entity.CameraPossessable;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class PossessionPlayerData implements PossessionComponent {

    private static final String POSSESSING_KEY = "Possessing";

    private final PlayerEntity player;
    private UUID possessedEntityUuid;
    private CameraPossessable possessedEntity;     // Kept on server-side only

    public PossessionPlayerData(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void setPossessedEntity(CameraPossessable entity) {
        if (entity.equals(possessedEntity)) {
            return;
        }
        this.possessedEntityUuid = entity.toEntity().getUuid();
        this.possessedEntity = entity;
        entity.onStartPossessing(player);
        ModComponents.POSSESSION.sync(player);
    }

    @Override
    public void resetPossessedEntity() {
        this.possessedEntityUuid = null;
        this.possessedEntity = null;
        ModComponents.POSSESSION.sync(player);
    }

    @Override
    @Nullable
    public CameraPossessable getPossessedEntity() {
        return possessedEntity;
    }

    @Override
    @Nullable
    public UUID getPossessedEntityUuid() {
        return possessedEntityUuid;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains(POSSESSING_KEY, NbtElement.INT_ARRAY_TYPE)) {
            this.possessedEntityUuid = tag.getUuid(POSSESSING_KEY);
            if (player.getWorld() instanceof ServerWorld serverWorld) {
                Entity entity = serverWorld.getEntity(this.possessedEntityUuid);
                if (entity instanceof CameraPossessable cameraPossessable) {
                    this.possessedEntity = cameraPossessable;
                } else {
                    Worldsinger.LOGGER.warn(
                            "Failed to set possessed entity since entity does not extend CameraPossessable");
                }
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if (possessedEntityUuid != null) {
            tag.putUuid(POSSESSING_KEY, possessedEntityUuid);
        }
    }
}
