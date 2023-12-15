package io.github.drakonkinst.worldsinger.registry;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.entity.Shapeshifter;
import io.github.drakonkinst.worldsinger.network.NetworkHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

public final class ClientNetworkHandler {

    public static void registerPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.SHAPESHIFTING_SYNC,
                (client, handler, buf, responseSender) -> {
                    final int id = buf.readVarInt();
                    final String entityId = buf.readString();
                    final NbtCompound entityNbt = buf.readNbt();

                    if (client.world == null) {
                        Worldsinger.LOGGER.warn("Skipping sync since world is unloaded");
                        return;
                    }

                    Entity entity = client.world.getEntityById(id);
                    if (entity == null) {
                        Worldsinger.LOGGER.warn(
                                "Skipping sync since entity with ID " + id + " does not exist");
                        return;
                    }
                    if (!(entity instanceof Shapeshifter shapeshifter)) {
                        Worldsinger.LOGGER.warn("Skipping sync since entity with ID " + id
                                + " exists but is not a shapeshifter");
                        return;
                    }

                    if (entityId.equals(Shapeshifter.EMPTY_MORPH)) {
                        shapeshifter.updateMorph(null);
                        return;
                    }

                    if (entityNbt != null) {
                        entityNbt.putString(Entity.ID_KEY, entityId);
                        Shapeshifter.createEntityFromNbt(shapeshifter, entityNbt, true);
                    }
                });
    }

    private ClientNetworkHandler() {}
}
