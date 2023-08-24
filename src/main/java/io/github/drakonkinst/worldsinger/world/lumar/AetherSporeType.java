package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.block.SporeEmitting;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.StringIdentifiable;

public enum AetherSporeType implements StringIdentifiable {
    DEAD("dead", 0x888888, 0xaaaaaa),
    VERDANT("verdant", 0x2e522e, 0x64aa4a);

    private static final float MAX_COLOR_VALUE = 255.0f;

    public static float getNormalizedRed(int color) {
        int red = (color >> 16) & 0xFF;
        return red / MAX_COLOR_VALUE;
    }

    public static float getNormalizedGreen(int color) {
        int green = (color >> 8) & 0xFF;
        return green / MAX_COLOR_VALUE;
    }

    public static float getNormalizedBlue(int color) {
        int blue = color & 0xFF;
        return blue / MAX_COLOR_VALUE;
    }

    public static Optional<AetherSporeType> getAetherSporeTypeFromBlock(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof SporeEmitting sporeEmittingBlock) {
            return Optional.of(sporeEmittingBlock.getSporeType());
        }
        return Optional.empty();
    }

    private final String name;
    private final int color;
    private final int particleColor;

    AetherSporeType(String name, int color, int particleColor) {
        this.name = name;
        this.color = color;
        this.particleColor = particleColor;
    }

    public int getColor() {
        return color;
    }

    public int getParticleColor() {
        return particleColor;
    }

    @Override
    public String asString() {
        return name;
    }
}
