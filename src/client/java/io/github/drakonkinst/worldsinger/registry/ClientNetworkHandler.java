package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.cosmere.ShapeshiftingManager;
import io.github.drakonkinst.worldsinger.entity.Shapeshifter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public final class ClientNetworkHandler {

    public static void registerPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(
                ShapeshiftingManager.SHAPESHIFTER_SYNC_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    ClientNetworkHandler.handleShapeshifterSyncPacket(client, buf);
                });
        ClientPlayNetworking.registerGlobalReceiver(
                ShapeshiftingManager.SHAPESHIFTER_ATTACK_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    ClientNetworkHandler.handleShapeshifterAttackPacket(client, buf);
                });
    }

    private static void handleShapeshifterSyncPacket(MinecraftClient client, PacketByteBuf buf) {
        final int id = buf.readVarInt();
        final String entityId = buf.readString();
        final NbtCompound entityNbt = buf.readNbt();

        if (client.world == null) {
            Worldsinger.LOGGER.warn("Failed to process sync packet since world is unloaded");
            return;
        }

        Entity entity = client.world.getEntityById(id);
        if (entity == null) {
            Worldsinger.LOGGER.warn(
                    "Failed to process sync packet since entity with ID " + id + " does not exist");
            return;
        }
        if (!(entity instanceof Shapeshifter shapeshifter)) {
            Worldsinger.LOGGER.warn("Failed to process sync packet since entity with ID " + id
                    + " exists but is not a shapeshifter");
            return;
        }

        if (entityId.equals(ShapeshiftingManager.EMPTY_MORPH)) {
            shapeshifter.updateMorph(null);
            return;
        }

        if (entityNbt != null) {
            entityNbt.putString(Entity.ID_KEY, entityId);
            ShapeshiftingManager.createMorphFromNbt(shapeshifter, entityNbt, true);
        }
    }

    private static void handleShapeshifterAttackPacket(MinecraftClient client, PacketByteBuf buf) {
        final int id = buf.readVarInt();

        if (client.world == null) {
            Worldsinger.LOGGER.warn("Failed to process attack packet since world is unloaded");
            return;
        }

        Entity entity = client.world.getEntityById(id);
        if (entity == null) {
            Worldsinger.LOGGER.warn("Failed to process attack packet since entity with ID " + id
                    + " does not exist");
            return;
        }
        if (!(entity instanceof Shapeshifter shapeshifter)) {
            Worldsinger.LOGGER.warn("Failed to process attack packet since entity with ID " + id
                    + " exists but is not a shapeshifter");
            return;
        }
        ShapeshiftingManager.onAttackClient(shapeshifter);
    }

    private ClientNetworkHandler() {}
}
