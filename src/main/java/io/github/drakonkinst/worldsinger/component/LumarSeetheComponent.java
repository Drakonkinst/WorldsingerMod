package io.github.drakonkinst.worldsinger.component;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LumarSeetheComponent implements SeetheComponent {

    private static final String NBT_TICKS_REMAINING = "ticksRemaining";
    private static final String NBT_CYCLES_UNTIL_NEXT_LONG_STILLING = "cyclesUntilNextLongStilling";
    private static final String NBT_IS_SEETHING = "isSeething";

    private static final int SECONDS_TO_TICKS = 20;
    private static final int MINUTES_TO_SECONDS = 60;
    private static final int GAME_DAYS_TO_MINUTES = 20;
    private static final int MINUTES_TO_TICKS = MINUTES_TO_SECONDS * SECONDS_TO_TICKS;
    private static final int GAME_DAYS_TO_TICKS = GAME_DAYS_TO_MINUTES * MINUTES_TO_TICKS;
    private static final IntProvider SEETHE_DURATION_PROVIDER = UniformIntProvider.create(
            5 * MINUTES_TO_TICKS, 2 * GAME_DAYS_TO_TICKS);
    private static final IntProvider STILLING_NORMAL_DURATION_PROVIDER = UniformIntProvider.create(
            2 * MINUTES_TO_TICKS, 5 * MINUTES_TO_TICKS);
    private static final IntProvider STILLING_LONG_DURATION_PROVIDER = BiasedToBottomIntProvider.create(
            10 * MINUTES_TO_TICKS, 30 * MINUTES_TO_TICKS);
    private static final IntProvider STILLING_LONG_CYCLE_PROVIDER = BiasedToBottomIntProvider.create(
            2, 5);

    private final Random random = Random.create();
    private final Object provider;
    private boolean isSeething;
    private int ticksRemaining;
    private int cyclesUntilLongStilling;

    public static boolean areSporesFluidized(World world) {
        return LumarSeetheComponent.areSporesFluidized(world.getScoreboard());
    }

    public static boolean areSporesFluidized(Scoreboard scoreboard) {
        return ModComponents.LUMAR_SEETHE.get(scoreboard).isSeething();
    }

    public LumarSeetheComponent(Scoreboard scoreboard, @Nullable MinecraftServer server) {
        this.provider = scoreboard;

        // Default values
        this.startSeethe();
        this.cyclesUntilLongStilling = STILLING_LONG_CYCLE_PROVIDER.get(this.random);
    }

    @Override
    public void serverTick() {
        if (ticksRemaining > 0) {
            --ticksRemaining;
        } else {
            if (isSeething) {
                this.stopSeethe();
            } else {
                this.startSeethe();
            }
        }
        ModComponents.LUMAR_SEETHE.sync(this.provider);
    }

    @Override
    public void startSeethe() {
        startSeethe(SEETHE_DURATION_PROVIDER.get(this.random));
    }

    @Override
    public void startSeethe(int ticks) {
        isSeething = true;
        ticksRemaining = ticks;
    }

    @Override
    public void stopSeethe() {
        int stillingTime;
        if (cyclesUntilLongStilling <= 0) {
            stillingTime = STILLING_LONG_DURATION_PROVIDER.get(this.random);
        } else {
            stillingTime = STILLING_NORMAL_DURATION_PROVIDER.get(this.random);
        }
        stopSeethe(stillingTime);
    }

    @Override
    public void stopSeethe(int ticks) {
        isSeething = false;
        if (cyclesUntilLongStilling <= 0) {
            cyclesUntilLongStilling = STILLING_LONG_CYCLE_PROVIDER.get(this.random);
        } else {
            --cyclesUntilLongStilling;
        }
        ticksRemaining = ticks;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        isSeething = tag.getBoolean(NBT_IS_SEETHING);
        ticksRemaining = tag.getInt(NBT_TICKS_REMAINING);
        cyclesUntilLongStilling = tag.getInt(NBT_CYCLES_UNTIL_NEXT_LONG_STILLING);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean(NBT_IS_SEETHING, isSeething);
        tag.putInt(NBT_TICKS_REMAINING, ticksRemaining);
        tag.putInt(NBT_CYCLES_UNTIL_NEXT_LONG_STILLING, cyclesUntilLongStilling);
    }

    @Override
    public boolean isSeething() {
        return isSeething;
    }

    @Override
    public int getTicksUntilNextCycle() {
        return ticksRemaining;
    }
}
