package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.cosmere.ShapeshiftingManager;
import java.util.Collections;
import java.util.UUID;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

public class PlayerMorphDummy extends LivingEntity {

    public static final String KEY_PLAYER = "Player";
    public static final String KEY_PLAYER_NAME = "PlayerName";
    private UUID playerUUID;
    private String playerName;

    public PlayerMorphDummy(World world, UUID uuid, String playerName) {
        super(EntityType.PLAYER, world);
        this.playerUUID = uuid;
        this.playerName = playerName;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        playerUUID = nbt.getUuid(KEY_PLAYER);
        playerName = nbt.getString(KEY_PLAYER_NAME);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putUuid(KEY_PLAYER, playerUUID);
        nbt.putString(KEY_PLAYER_NAME, playerName);
        return nbt;
    }

    @Override
    public boolean saveSelfNbt(NbtCompound nbt) {
        nbt.putString("id", ShapeshiftingManager.ID_PLAYER);
        writeNbt(nbt);
        return true;
    }

    @Override
    protected Text getDefaultName() {
        return Text.literal(playerName);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        // Do nothing
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }
}
