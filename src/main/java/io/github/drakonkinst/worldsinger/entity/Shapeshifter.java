package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public interface Shapeshifter {

    String EMPTY_MORPH = "minecraft:empty";

    static void syncToNearbyPlayers(ServerWorld world, Shapeshifter shapeshifter) {
        Packet<ClientCommonPacketListener> packet = ServerPlayNetworking.createS2CPacket(
                NetworkHandler.SHAPESHIFTING_SYNC, Shapeshifter.createPacket(shapeshifter));
        world.getChunkManager().sendToNearbyPlayers(shapeshifter.toEntity(), packet);
    }

    static void syncToPlayer(ServerPlayerEntity playerEntity, Shapeshifter shapeshifter) {
        ServerPlayNetworking.send(playerEntity, NetworkHandler.SHAPESHIFTING_SYNC,
                Shapeshifter.createPacket(shapeshifter));
    }

    private static PacketByteBuf createPacket(Shapeshifter shapeshifter) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        NbtCompound entityTag = new NbtCompound();

        LivingEntity morph = shapeshifter.getMorph();
        if (morph != null) {
            morph.writeNbt(entityTag);
        }

        LivingEntity shapeshifterEntity = shapeshifter.toEntity();
        buf.writeVarInt(shapeshifterEntity.getId());
        buf.writeString(morph == null ? EMPTY_MORPH
                : Registries.ENTITY_TYPE.getId(morph.getType()).toString());
        buf.writeNbt(entityTag);
        return buf;
    }

    static void createEntityFromNbt(Shapeshifter shapeshifter, NbtCompound morphNbt) {
        Optional<EntityType<?>> type = EntityType.fromNbt(morphNbt);
        if (type.isPresent()) {
            LivingEntity morph = shapeshifter.getMorph();
            if (morph == null || !type.get().equals(shapeshifter.toEntity().getType())) {
                morph = (LivingEntity) type.get().create(shapeshifter.toEntity().getWorld());
            }

            if (morph != null) {
                shapeshifter.onMorphEntitySpawn(morph);
                morph.readNbt(morphNbt);
                shapeshifter.updateMorph(morph);
            }
        }
    }

    void setMorph(@Nullable LivingEntity morph);

    // Should implicitly call setMorph()
    void updateMorph(@Nullable LivingEntity morph);

    default void onMorphEntitySpawn(LivingEntity morph) {
        // Do nothing
    }

    @Nullable LivingEntity getMorph();

    LivingEntity toEntity();
}
