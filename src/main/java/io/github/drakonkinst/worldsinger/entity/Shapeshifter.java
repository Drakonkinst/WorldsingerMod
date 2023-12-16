package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.network.NetworkHandler;
import io.netty.buffer.Unpooled;
import java.util.Optional;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.SlimeEntity;
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

    static void createMorphFromNbt(Shapeshifter shapeshifter, NbtCompound morphNbt,
            boolean showTransformEffects) {
        Optional<EntityType<?>> type = EntityType.fromNbt(morphNbt);
        if (type.isPresent()) {
            LivingEntity morph = shapeshifter.getMorph();
            if (morph == null || !type.get().equals(morph.getType())) {
                // TODO: Players have no entity factory and cannot be created, so this is null?
                morph = (LivingEntity) type.get().create(shapeshifter.toEntity().getWorld());
            }

            if (morph != null) {
                shapeshifter.onMorphEntitySpawn(morph);
                morph.readNbt(morphNbt);
                shapeshifter.updateMorph(morph);
                shapeshifter.afterMorphEntitySpawn(morph, showTransformEffects);
            }
        }
    }

    static void createMorphFromEntity(Shapeshifter shapeshifter, LivingEntity toCopy,
            boolean showTransformEffects) {
        LivingEntity morph = shapeshifter.getMorph();

        if (morph == null || toCopy.getType().equals(morph.getType())) {
            morph = (LivingEntity) toCopy.getType().create(shapeshifter.toEntity().getWorld());
        }

        if (morph != null) {
            shapeshifter.onMorphEntitySpawn(morph);
            Shapeshifter.copyVariantData(shapeshifter, morph, toCopy);
            shapeshifter.updateMorph(morph);
            shapeshifter.afterMorphEntitySpawn(morph, showTransformEffects);
        }
    }

    // Can add more variant data here
    // Assumes morph and toCopy are of the same type
    private static void copyVariantData(Shapeshifter shapeshifter, LivingEntity morph,
            LivingEntity toCopy) {
        // Baby
        if (morph instanceof MobEntity mobMorph && toCopy instanceof MobEntity mobToCopy) {
            mobMorph.setBaby(mobToCopy.isBaby());
        }

        // Slime/Magma Cube Size
        if (morph instanceof SlimeEntity slimeMorph && toCopy instanceof SlimeEntity slimeToCopy) {
            slimeMorph.setSize(slimeToCopy.getSize(), false);
        }

        // Phantom Size
        if (morph instanceof PhantomEntity phantomMorph
                && toCopy instanceof PhantomEntity phantomToCopy) {
            phantomMorph.setPhantomSize(phantomToCopy.getPhantomSize());
        }

        if (shapeshifter.copyEquipmentVisuals()) {
            // Equipped items
            morph.equipStack(EquipmentSlot.MAINHAND,
                    toCopy.getEquippedStack(EquipmentSlot.MAINHAND));
            morph.equipStack(EquipmentSlot.OFFHAND, toCopy.getEquippedStack(EquipmentSlot.OFFHAND));
            morph.equipStack(EquipmentSlot.HEAD, toCopy.getEquippedStack(EquipmentSlot.HEAD));
            morph.equipStack(EquipmentSlot.CHEST, toCopy.getEquippedStack(EquipmentSlot.CHEST));
            morph.equipStack(EquipmentSlot.LEGS, toCopy.getEquippedStack(EquipmentSlot.LEGS));
            morph.equipStack(EquipmentSlot.FEET, toCopy.getEquippedStack(EquipmentSlot.FEET));
        }
    }

    void setMorph(@Nullable LivingEntity morph);

    // Should implicitly call setMorph()
    void updateMorph(@Nullable LivingEntity morph);

    default void onMorphEntitySpawn(LivingEntity morph) {
        // Do nothing
    }

    // showTransformEffects = whether the entity has just spawned. Used to determine whether particles should spawn or not.
    default void afterMorphEntitySpawn(LivingEntity morph, boolean showTransformEffects) {
        // Do nothing
    }

    default boolean copyEquipmentVisuals() {
        return false;
    }

    @Nullable LivingEntity getMorph();

    LivingEntity toEntity();
}
