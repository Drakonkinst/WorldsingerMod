package io.github.drakonkinst.worldsinger.world.lumar;

import com.google.common.base.Suppliers;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.block.SporeEmitting;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.fluid.AetherSporeFluid;
import io.github.drakonkinst.worldsinger.fluid.ModFluids;
import io.github.drakonkinst.worldsinger.item.ModItems;
import io.github.drakonkinst.worldsinger.item.SporeBottleItem;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;

public enum AetherSporeType implements SporeType {
    DEAD("dead", 0x888888, 0xaaaaaa, () -> null,
            () -> ModItems.DEAD_SPORES_BOTTLE, () -> ModItems.DEAD_SPORES_BUCKET,
            () -> ModFluids.DEAD_SPORES,
            () -> ModBlocks.DEAD_SPORE_SEA, () -> ModBlocks.DEAD_SPORE_BLOCK),
    VERDANT("verdant", 0x2e522e, 0x64aa4a, () -> ModStatusEffects.VERDANT_SPORES,
            () -> ModItems.VERDANT_SPORES_BOTTLE, () -> ModItems.VERDANT_SPORES_BUCKET,
            () -> ModFluids.VERDANT_SPORES,
            () -> ModBlocks.VERDANT_SPORE_SEA, () -> ModBlocks.VERDANT_SPORE_BLOCK),
    CRIMSON("crimson", 0xd72e2d, 0xe44d61, () -> ModStatusEffects.CRIMSON_SPORES,
            () -> ModItems.CRIMSON_SPORES_BOTTLE, () -> ModItems.CRIMSON_SPORES_BUCKET,
            () -> ModFluids.CRIMSON_SPORES,
            () -> ModBlocks.CRIMSON_SPORE_SEA, () -> ModBlocks.CRIMSON_SPORE_BLOCK),
    ZEPHYR("zephyr", 0x4b9bb7, 0x4b9bb7, () -> ModStatusEffects.ZEPHYR_SPORES,
            () -> ModItems.ZEPHYR_SPORES_BOTTLE, () -> ModItems.ZEPHYR_SPORES_BUCKET,
            () -> ModFluids.ZEPHYR_SPORES,
            () -> ModBlocks.ZEPHYR_SPORE_SEA, () -> ModBlocks.ZEPHYR_SPORE_BLOCK),
    SUNLIGHT("sunlight", 0xf4bd52, 0xf4bd52, () -> ModStatusEffects.SUNLIGHT_SPORES,
            () -> ModItems.SUNLIGHT_SPORES_BOTTLE, () -> ModItems.SUNLIGHT_SPORES_BUCKET,
            () -> ModFluids.SUNLIGHT_SPORES,
            () -> ModBlocks.SUNLIGHT_SPORE_SEA, () -> ModBlocks.SUNLIGHT_SPORE_BLOCK),
    // TODO
    ROSEITE("roseite", 0xce9db2, 0xce9db2, () -> ModStatusEffects.ROSEITE_SPORES,
            () -> ModItems.ROSEITE_SPORES_BOTTLE, () -> null,
            () -> null,
            () -> null, () -> null),
    // TODO
    MIDNIGHT("midnight", 0x111111, 0x111111, () -> ModStatusEffects.MIDNIGHT_SPORES,
            () -> ModItems.MIDNIGHT_SPORES_BOTTLE, () -> null,
            () -> null,
            () -> null, () -> null);

    public static final BasicCodec<AetherSporeType> CODEC = StringIdentifiable.createCodec(
            AetherSporeType::values);
    private static final float MAX_COLOR_VALUE = 255.0f;
    private final String name;
    private final int color;
    private final int particleColor;
    private final Supplier<StatusEffect> statusEffect;
    private final Supplier<Item> bottledItem;
    private final Supplier<Item> bucketItem;
    private final Supplier<FlowableFluid> fluid;
    private final Supplier<Block> fluidBlock;
    private final Supplier<Block> solidBlock;

    AetherSporeType(String name, int color, int particleColor,
            Supplier<StatusEffect> statusEffect, Supplier<Item> bottledItem,
            Supplier<Item> bucketItem, Supplier<FlowableFluid> fluid, Supplier<Block> fluidBlock,
            Supplier<Block> solidBlock) {

        this.name = name;
        this.color = color;
        this.particleColor = particleColor;
        this.statusEffect = Suppliers.memoize(statusEffect::get);
        this.bottledItem = Suppliers.memoize(bottledItem::get);
        this.bucketItem = Suppliers.memoize(bucketItem::get);
        this.fluid = Suppliers.memoize(fluid::get);
        this.fluidBlock = Suppliers.memoize(fluidBlock::get);
        this.solidBlock = Suppliers.memoize(solidBlock::get);
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

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public int getParticleColor() {
        return particleColor;
    }

    @Override
    public StatusEffect getStatusEffect() {
        return statusEffect.get();
    }

    @Override
    public Item getBottledItem() {
        return bottledItem.get();
    }

    @Override
    public Item getBucketItem() {
        return bucketItem.get();
    }

    @Override
    public FlowableFluid getFluid() {
        return fluid.get();
    }

    @Override
    public Block getFluidBlock() {
        return fluidBlock.get();
    }

    @Override
    public Block getSolidBlock() {
        return solidBlock.get();
    }

    @Override
    public String asString() {
        return name;
    }
}
