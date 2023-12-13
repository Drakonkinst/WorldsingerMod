package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public interface Shapeshifter {

    String EMPTY_IDENTITY = "minecraft:empty";

    static void sync(ServerWorld world, Shapeshifter shapeshifter) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        NbtCompound entityTag = new NbtCompound();

        LivingEntity identity = shapeshifter.getIdentity();
        if (identity != null) {
            identity.writeNbt(entityTag);
        }

        LivingEntity shapeshifterEntity = shapeshifter.toEntity();
        buf.writeUuid(shapeshifterEntity.getUuid());
        buf.writeString(identity == null ? EMPTY_IDENTITY
                : Registries.ENTITY_TYPE.getId(identity.getType()).toString());
        buf.writeNbt(entityTag);

        Worldsinger.LOGGER.info("SENDING SYNC");
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(
                NetworkHandler.SHAPESHIFTING_SYNC, buf);
        world.getChunkManager().sendToNearbyPlayers(shapeshifterEntity, packet);
    }

    void setIdentity(@Nullable LivingEntity identity);

    void updateIdentity(@Nullable LivingEntity identity);

    @Nullable LivingEntity getIdentity();

    LivingEntity toEntity();
}
