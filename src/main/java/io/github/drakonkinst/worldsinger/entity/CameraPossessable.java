package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

// Indicates a mob that can be possessed by the player, which changes the camera client-side only.
// Should use PossessionComponent
public interface CameraPossessable {

    int FIRST_PERSON = 0;
    int THIRD_PERSON_BACK = 1;
    int THIRD_PERSON_FRONT = 2;

    Identifier POSSESS_UPDATE_PACKET_ID = Worldsinger.id("possession_update");

    static PacketByteBuf createSyncPacket(float headYaw, float bodyYaw, float pitch) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeFloat(headYaw);
        buf.writeFloat(bodyYaw);
        buf.writeFloat(pitch);
        return buf;
    }

    void setLookDir(float headYaw, float bodyYaw, float pitch);

    void onStartPossessing(PlayerEntity possessor);

    boolean shouldKeepPossessing(PlayerEntity possessor);

    Entity toEntity();

    default int getDefaultPerspective() {
        return FIRST_PERSON;
    }

    default boolean canAttack() {
        return false;
    }

    default boolean canPickBlock() {
        return false;
    }

    default boolean canBreakBlock() {
        return false;
    }

    default boolean canInteractWithEntities() {
        return false;
    }

    default boolean canInteractWithBlocks() {
        return false;
    }

    default boolean canSwitchPerspectives() {
        return false;
    }
}
