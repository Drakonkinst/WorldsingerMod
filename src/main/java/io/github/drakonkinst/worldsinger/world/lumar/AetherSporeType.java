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
    DEAD("dead", 0x888888, 0xaaaaaa,
            () -> null, () -> ModItems.DEAD_SPORES_BOTTLE),
    VERDANT("verdant", 0x2e522e, 0x64aa4a,
            () -> ModStatusEffects.VERDANT_SPORES, () -> ModItems.VERDANT_SPORES_BOTTLE),
    CRIMSON("crimson", 0xd72e2d, 0xe44d61,
            () -> ModStatusEffects.CRIMSON_SPORES, () -> ModItems.CRIMSON_SPORES_BOTTLE),
    ZEPHYR("zephyr", 0x4b9bb7, 0x4b9bb7,
            () -> ModStatusEffects.ZEPHYR_SPORES, () -> ModItems.ZEPHYR_SPORES_BOTTLE),
    SUNLIGHT("sunlight", 0xf4bd52, 0xf4bd52,
            () -> ModStatusEffects.SUNLIGHT_SPORES, () -> ModItems.SUNLIGHT_SPORES_BOTTLE),
    ROSEITE("roseite", 0xce9db2, 0xce9db2,
            () -> ModStatusEffects.ROSEITE_SPORES, () -> ModItems.ROSEITE_SPORES_BOTTLE),
    MIDNIGHT("midnight", 0x111111, 0x111111,
            () -> ModStatusEffects.MIDNIGHT_SPORES, () -> ModItems.MIDNIGHT_SPORES_BOTTLE);

    public static final BasicCodec<AetherSporeType> CODEC = StringIdentifiable.createCodec(
            AetherSporeType::values);
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
            return sporeBottleItem.getSporeType().getColor();
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
