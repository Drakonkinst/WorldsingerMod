package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.util.Constants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class LumarSeetheData {

    public static final Identifier LUMAR_SEETHE_UPDATE_PACKET_ID = new Identifier(Constants.MOD_ID,
            "lumar_seethe_state");
    private static final String CYCLE_TICKS_NBT = "cycleTick";
    private static final String CYCLES_UNTIL_NEXT_LONG_STILLING_NBT = "cyclesUntilNextLongStilling";
    private static final String IS_SEETHING_NBT = "isSeething";
    private int cyclesUntilNextLongStilling;
    private int cycleTicks;
    private boolean isSeething;

    public static LumarSeetheData fromNbt(NbtCompound tag) {
        LumarSeetheData data = new LumarSeetheData();
        data.isSeething = tag.getBoolean(IS_SEETHING_NBT);
        data.cycleTicks = tag.getInt(CYCLE_TICKS_NBT);
        data.cyclesUntilNextLongStilling = tag.getInt(CYCLES_UNTIL_NEXT_LONG_STILLING_NBT);
        return data;
    }

    public static NbtCompound writeNbt(LumarSeetheData lumarSeetheData, NbtCompound nbt) {
        nbt.putBoolean(IS_SEETHING_NBT, lumarSeetheData.isSeething);
        nbt.putInt(CYCLE_TICKS_NBT, lumarSeetheData.cycleTicks);
        nbt.putInt(CYCLES_UNTIL_NEXT_LONG_STILLING_NBT,
                lumarSeetheData.cyclesUntilNextLongStilling);
        return nbt;
    }

    public static LumarSeetheData fromBuf(PacketByteBuf buf) {
        LumarSeetheData data = new LumarSeetheData();

        data.setSeething(buf.readBoolean());
        data.setCycleTicks(buf.readInt());
        data.setCyclesUntilNextLongStilling(buf.readInt());

        return data;
    }

    public int getCyclesUntilNextLongStilling() {
        return cyclesUntilNextLongStilling;
    }

    public void setCyclesUntilNextLongStilling(int cyclesUntilNextLongStilling) {
        this.cyclesUntilNextLongStilling = cyclesUntilNextLongStilling;
    }

    public int getCycleTicks() {
        return cycleTicks;
    }

    public void setCycleTicks(int cycleTicks) {
        this.cycleTicks = cycleTicks;
    }

    public boolean isSeething() {
        return isSeething;
    }

    public void setSeething(boolean seething) {
        isSeething = seething;
    }

    public void sendToClient(World world) {
        if (world.isClient()) {
            return;
        }

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(isSeething);
        buf.writeInt(cycleTicks);
        buf.writeInt(cyclesUntilNextLongStilling);

        for (ServerPlayerEntity player : PlayerLookup.world((ServerWorld) world)) {
            ServerPlayNetworking.send(player, LUMAR_SEETHE_UPDATE_PACKET_ID, buf);
        }
    }

    public void copy(LumarSeetheData data) {
        this.setSeething(data.isSeething());
        this.setCycleTicks(data.getCycleTicks());
        this.setCyclesUntilNextLongStilling(data.getCyclesUntilNextLongStilling());
    }
}
