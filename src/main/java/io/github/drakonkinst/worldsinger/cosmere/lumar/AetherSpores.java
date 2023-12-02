package io.github.drakonkinst.worldsinger.cosmere.lumar;

import com.mojang.serialization.Codec;
import io.github.drakonkinst.worldsinger.block.SporeEmitting;
import io.github.drakonkinst.worldsinger.fluid.AetherSporeFluid;
import io.github.drakonkinst.worldsinger.item.SporeBottleItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AetherSpores {

    public static final Map<String, AetherSpores> AETHER_SPORE_MAP = new Object2ObjectArrayMap<>();
    public static final Codec<AetherSpores> CODEC = Codecs.idChecked(AetherSpores::getName,
            AETHER_SPORE_MAP::get);

    public static int getBottleColor(ItemStack stack) {
        if (stack.getItem() instanceof SporeBottleItem sporeBottleItem) {
            return sporeBottleItem.getSporeType().getColor();
        }
        return -1;
    }

    public static Optional<AetherSpores> getSporeTypeFromBlock(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof SporeEmitting sporeEmittingBlock) {
            return Optional.of(sporeEmittingBlock.getSporeType());
        }
        return Optional.empty();
    }

    public static Optional<AetherSpores> getFirstSporeTypeFromFluid(Collection<Fluid> fluids) {
        for (Fluid fluid : fluids) {
            if (fluid instanceof AetherSporeFluid aetherSporeFluid) {
                return Optional.of(aetherSporeFluid.getSporeType());
            }
        }
        return Optional.empty();
    }

    protected AetherSpores() {
        AETHER_SPORE_MAP.put(this.getName(), this);
    }

    public abstract int getColor();

    public abstract String getName();

    public abstract void onDeathFromStatusEffect(World world, LivingEntity entity, BlockPos pos,
            int water);

    public abstract Item getBottledItem();

    public abstract Item getBucketItem();

    public abstract Block getFluidBlock();

    public abstract Block getSolidBlock();

    public abstract FlowableFluid getFluid();

    @Nullable
    public abstract StatusEffect getStatusEffect();

    public abstract int getParticleColor();

    public abstract int getId();

    // By default, act as normal
    public void doReactionFromFluidContainer(World world, BlockPos fluidContainerPos, int spores,
            int water, Random random) {
        this.doReaction(world, fluidContainerPos, spores, water, random);
    }

    public final void doReaction(World world, BlockPos pos, int spores, int water, Random random) {
        this.doReaction(world, pos.toCenterPos(), spores, water, random);
    }

    public abstract void doReaction(World world, Vec3d pos, int spores, int water, Random random);

    // By default, act as normal
    public void doReactionFromSplashBottle(World world, Vec3d pos, int spores, int water,
            Random random, boolean affectingFluidContainer) {
        this.doReaction(world, pos, spores, water, random);
    }

    @Nullable
    public BlockState getFluidCollisionState() {
        return null;
    }

    public boolean isDead() {
        return false;
    }

    // Do a little hack to move spore growth position to the topmost block
    protected Vec3d getTopmostSeaPosForEntity(World world, LivingEntity entity,
            TagKey<Fluid> fluidTag) {
        BlockPos.Mutable mutable = entity.getBlockPos().mutableCopy();

        while (world.getFluidState(mutable).isIn(fluidTag) && mutable.getY() < world.getTopY()) {
            mutable.move(Direction.UP);
        }

        if (world.getBlockState(mutable).isAir()) {
            // Found a good position, use it
            return mutable.move(Direction.DOWN).toCenterPos();
        } else {
            // Use original position
            return entity.getPos();
        }
    }
}
