package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.block.SporeEmitting;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.fluid.AetherSporeFluid;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.item.SporeBottleItem;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;

public enum AetherSporeType implements StringIdentifiable {
    DEAD("dead", 0x888888, 0xaaaaaa, () -> null, () -> ModItems.DEAD_SPORES_BOTTLE),
    VERDANT("verdant", 0x2e522e, 0x64aa4a, () -> ModStatusEffects.VERDANT_SPORES,
            () -> ModItems.VERDANT_SPORES_BOTTLE);

    private static final float MAX_COLOR_VALUE = 255.0f;
    private final String name;
    private final int color;
    private final int particleColor;
    private final Supplier<StatusEffect> statusEffect;
    private final Supplier<Item> bottledItem;

    AetherSporeType(String name, int color, int particleColor,
            Supplier<StatusEffect> statusEffect, Supplier<Item> bottledItem) {
        this.name = name;
        this.color = color;
        this.particleColor = particleColor;
        this.statusEffect = statusEffect;
        this.bottledItem = bottledItem;
    }

    public static int getBottleColor(ItemStack stack) {
        if (stack.getItem() instanceof SporeBottleItem sporeBottleItem) {
            return sporeBottleItem.getSporeType().getParticleColor();
        }
        return -1;
    }

    public static Optional<AetherSporeType> getSporeTypeFromBlock(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof SporeEmitting sporeEmittingBlock) {
            return Optional.of(sporeEmittingBlock.getSporeType());
        }
        return Optional.empty();
    }

    public static Optional<AetherSporeType> getFirstSporeTypeFromFluid(Collection<Fluid> fluids) {
        for (Fluid fluid : fluids) {
            if (fluid instanceof AetherSporeFluid aetherSporeFluid) {
                return Optional.of(aetherSporeFluid.getSporeType());
            }
        }
        return Optional.empty();
    }

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

    public int getColor() {
        return color;
    }

    public int getParticleColor() {
        return particleColor;
    }

    public StatusEffect getStatusEffect() {
        return statusEffect.get();
    }

    public Item getBottledItem() {
        return bottledItem.get();
    }

    @Override
    public String asString() {
        return name;
    }
}
