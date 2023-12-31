package io.github.drakonkinst.worldsinger.network;

import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.PossessionComponent;
import io.github.drakonkinst.worldsinger.entity.CameraPossessable;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerNetworkHandler {

    public static void registerPacketHandler() {
        ServerPlayNetworking.registerGlobalReceiver(CameraPossessable.POSSESS_UPDATE_PACKET_ID,
                ((server, player, handler, buf, responseSender) -> {
                    // Get corresponding entity
                    PossessionComponent possessionData = ModComponents.POSSESSION.get(player);
                    CameraPossessable possessedEntity = possessionData.getPossessedEntity();
                    if (possessedEntity != null) {
                        float headYaw = buf.readFloat();
                        float bodyYaw = buf.readFloat();
                        float pitch = buf.readFloat();
                        possessedEntity.setLookDir(headYaw, bodyYaw, pitch);
                    }
                }));
    }
}
