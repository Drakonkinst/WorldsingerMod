package io.github.drakonkinst.worldsinger.client.registry;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.entity.Shapeshifter;
import io.github.drakonkinst.worldsinger.network.NetworkHandler;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

public final class ClientNetworkHandler {

    public static void registerPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.SHAPESHIFTING_SYNC,
                (client, handler, buf, responseSender) -> {
                    // TODO
                    Worldsinger.LOGGER.info("Shapeshifting sync packet received");
                    final UUID uuid = buf.readUuid();
                    final String id = buf.readString();
                    final NbtCompound entityNbt = buf.readNbt();

                    if (client.world == null) {
                        return;
                    }

                    Shapeshifter targetEntity = null;
                    for (Entity entity : client.world.getEntities()) {
                        if (entity.getUuid().equals(uuid)
                                && entity instanceof Shapeshifter shapeshifter) {
                            targetEntity = shapeshifter;
                            break;
                        }
                    }

                    if (targetEntity == null) {
                        return;
                    }

                    if (id.equals(Shapeshifter.EMPTY_IDENTITY)) {
                        targetEntity.updateIdentity(null);
                        return;
                    }

                    if (entityNbt != null) {
                        entityNbt.putString(Entity.ID_KEY, id);
                        Optional<EntityType<?>> type = EntityType.fromNbt(entityNbt);
                        if (type.isPresent()) {
                            LivingEntity identity = targetEntity.getIdentity();

                            if (identity == null || !type.get().equals(identity.getType())) {
                                identity = (LivingEntity) type.get()
                                        .create(targetEntity.toEntity().getWorld());
                                targetEntity.updateIdentity(identity);
                            }

                            if (identity != null) {
                                identity.readNbt(entityNbt);
                            }
                        }
                    }
                });
    }

    private ClientNetworkHandler() {}
}
