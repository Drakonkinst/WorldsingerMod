package io.github.drakonkinst.worldsinger.effect;

import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.fluid.Fluidlogged;
import io.github.drakonkinst.worldsinger.util.ModProperties;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SporeStatusEffect extends StatusEffect {

    public static final float DEFAULT_DAMAGE = 15.0f;

    private static BlockPos toRoundedBlockPos(Vec3d pos) {
        int x = (int) Math.round(pos.getX());
        int y = (int) Math.round(pos.getY());
        int z = (int) Math.round(pos.getZ());
        return new BlockPos(x, y, z);
    }

    private final AetherSporeType aetherSporeType;
    private final float damage;

    protected SporeStatusEffect(AetherSporeType aetherSporeType) {
        this(aetherSporeType, DEFAULT_DAMAGE);
    }

    protected SporeStatusEffect(AetherSporeType aetherSporeType, float damage) {
        super(StatusEffectCategory.HARMFUL, aetherSporeType.getColor());
        this.aetherSporeType = aetherSporeType;
        this.damage = damage;
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        boolean wasDamaged = entity.damage(entity.getDamageSources().drown(),
                damage);
        if (wasDamaged && entity.isDead()) {
            onDeathEffect(entity);
        }
    }

    private void onDeathEffect(LivingEntity entity) {
        World world = entity.getWorld();
        if (aetherSporeType == AetherSporeType.VERDANT) {
            // TODO: Replace with a small spore growth entity
            growVerdantSpores(world, entity);
        }
    }

    private void growVerdantSpores(World world, LivingEntity entity) {
        BlockPos blockPos = toRoundedBlockPos(entity.getPos());
        BlockPos.Mutable currentBlockPos = new BlockPos.Mutable(blockPos.getX(), blockPos.getY(),
                blockPos.getZ());
        for (int i = 0; i < 2; ++i) {
            BlockState blockState = world.getBlockState(currentBlockPos);
            BlockState newBlockState = ModBlocks.VERDANT_VINE_SNARE.getDefaultState()
                    .with(ModProperties.FLUIDLOGGED, Fluidlogged.getFluidIndex(
                            blockState.getFluidState().getFluid()))
                    .with(Properties.PERSISTENT, false);
            if (blockState.isIn(ModBlockTags.SPORES_CAN_GROW) || blockState.isOf(
                    ModBlocks.VERDANT_SPORE_SEA_BLOCK) && newBlockState.canPlaceAt(world,
                    currentBlockPos)) {
                world.setBlockState(currentBlockPos, newBlockState);
            }
            currentBlockPos.move(Direction.UP);
        }
    }

    public AetherSporeType getSporeType() {
        return aetherSporeType;
    }
}
