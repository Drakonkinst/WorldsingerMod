package io.github.drakonkinst.worldsinger.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public final class LumarSeetheManager extends PersistentState {

    private static final int SECONDS_TO_TICKS = 20;
    private static final int MINUTES_TO_SECONDS = 60;
    private static final int GAME_DAYS_TO_MINUTES = 20;
    private static final int MINUTES_TO_TICKS = MINUTES_TO_SECONDS * SECONDS_TO_TICKS;
    private static final int GAME_DAYS_TO_TICKS = GAME_DAYS_TO_MINUTES * MINUTES_TO_TICKS;
    private static final IntProvider LUMAR_SEETHE_DURATION_PROVIDER = UniformIntProvider.create(
            5 * MINUTES_TO_TICKS, 2 * GAME_DAYS_TO_TICKS);
    private static final IntProvider LUMAR_STILLING_NORMAL_DURATION_PROVIDER = UniformIntProvider.create(
            2 * MINUTES_TO_TICKS, 5 * MINUTES_TO_TICKS);
    private static final IntProvider LUMAR_STILLING_LONG_DURATION_PROVIDER = BiasedToBottomIntProvider.create(
            10 * MINUTES_TO_TICKS, 30 * MINUTES_TO_TICKS);
    private static final IntProvider LUMAR_STILLING_LONG_CYCLE_PROVIDER = BiasedToBottomIntProvider.create(
            2, 5);

    public final Random random = Random.create();
    private final ServerWorld world;
    private final LumarSeetheData lumarSeetheData;

    public LumarSeetheManager(ServerWorld world, LumarSeetheData lumarSeetheData) {
        this.world = world;
        this.lumarSeetheData = lumarSeetheData;

        startSeething();
        this.resetCyclesUntilNextLongStilling();
    }

    public void tick() {
        if (lumarSeetheData.getCycleTicks() > 0) {
            lumarSeetheData.setCycleTicks(lumarSeetheData.getCycleTicks() - 1);
        } else {
            if (lumarSeetheData.isSeething()) {
                startStilling();
            } else {
                startSeething();
            }
        }

        this.sendToClient();
        this.markDirty();
    }

    public void sendToClient() {
        lumarSeetheData.sendToClient(this.world);
    }

    public void startSeething() {
        startSeething(LUMAR_SEETHE_DURATION_PROVIDER.get(this.random));
    }

    public void startStilling() {
        int stillingTime;
        if (lumarSeetheData.getCyclesUntilNextLongStilling() <= 0) {
            stillingTime = LUMAR_STILLING_LONG_DURATION_PROVIDER.get(this.random);
        } else {
            stillingTime = LUMAR_STILLING_NORMAL_DURATION_PROVIDER.get(this.random);
        }
        startStilling(stillingTime);
    }

    public void startSeething(int cycleTicks) {
        lumarSeetheData.setSeething(true);
        lumarSeetheData.setCycleTicks(cycleTicks);
    }

    private void resetCyclesUntilNextLongStilling() {
        lumarSeetheData.setCyclesUntilNextLongStilling(
                LUMAR_STILLING_LONG_CYCLE_PROVIDER.get(this.random));
    }

    public void startStilling(int cycleTicks) {
        lumarSeetheData.setSeething(false);
        if (lumarSeetheData.getCyclesUntilNextLongStilling() <= 0) {
            resetCyclesUntilNextLongStilling();
        } else {
            lumarSeetheData.setCyclesUntilNextLongStilling(
                    lumarSeetheData.getCyclesUntilNextLongStilling() - 1);
        }
        lumarSeetheData.setCycleTicks(cycleTicks);
    }

    public LumarSeetheData getLumarSeetheData() {
        return lumarSeetheData;
    }

    public static boolean areSporesFluidized(World world) {
        return ((LumarSeetheAccess) world).worldsinger$getLumarSeetheData().isSeething();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        return LumarSeetheData.writeNbt(lumarSeetheData, nbt);
    }

    public static LumarSeetheManager fromNbt(ServerWorld world, NbtCompound nbt) {
        return new LumarSeetheManager(world, LumarSeetheData.fromNbt(nbt));
    }
}
