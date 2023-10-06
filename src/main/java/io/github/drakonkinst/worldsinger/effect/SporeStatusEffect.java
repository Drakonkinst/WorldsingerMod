package io.github.drakonkinst.worldsinger.effect;

import io.github.drakonkinst.worldsinger.block.LivingAetherSporeBlock;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.block.ModBlocks;
import io.github.drakonkinst.worldsinger.block.SporeEmitting;
import io.github.drakonkinst.worldsinger.entity.SporeFluidEntityStateAccess;
import io.github.drakonkinst.worldsinger.entity.SporeGrowthEntity;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.registry.ModDamageTypes;
import io.github.drakonkinst.worldsinger.util.BlockPosUtil;
import io.github.drakonkinst.worldsinger.world.lumar.AetherSporeType;
import io.github.drakonkinst.worldsinger.world.lumar.SporeGrowthSpawner;
import io.github.drakonkinst.worldsinger.world.lumar.SporeParticleManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SporeStatusEffect extends StatusEffect implements SporeEmitting {

    public static final float DEFAULT_DAMAGE = 15.0f;
    private static final int WATER_PER_ENTITY_BLOCK = 75;
    private final AetherSporeType aetherSporeType;
    private final RegistryKey<DamageType> damageType;
    private final float damageAmount;

    protected SporeStatusEffect(AetherSporeType aetherSporeType,
            RegistryKey<DamageType> damageType) {
        this(aetherSporeType, DEFAULT_DAMAGE, damageType);
    }

    protected SporeStatusEffect(AetherSporeType aetherSporeType, float damageAmount,
            RegistryKey<DamageType> damageType) {
        super(StatusEffectCategory.HARMFUL, aetherSporeType.getParticleColor());
        this.aetherSporeType = aetherSporeType;
        this.damageAmount = damageAmount;
        this.damageType = damageType;
    }


    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!SporeParticleManager.sporesCanAffect(entity)) {
            return;
        }
        boolean wasDamaged = entity.damage(ModDamageTypes.of(entity.getWorld(), damageType),
                damageAmount);
        if (wasDamaged && entity.isDead()) {
            onDeathEffect(entity);
        }
    }

    private void onDeathEffect(LivingEntity entity) {
        World world = entity.getWorld();
        if (aetherSporeType == AetherSporeType.VERDANT) {
            // Fill with snare blocks
            this.growVerdantSpores(world, entity);

            // Only spawn spore growth if in the spore sea
            if (!world.getFluidState(entity.getBlockPos()).isIn(ModFluidTags.VERDANT_SPORES)
                    || !((SporeFluidEntityStateAccess) entity).worldsinger$isTouchingSporeSea()) {
                return;
            }

            int waterAmount = MathHelper.ceil(
                    entity.getWidth() * entity.getHeight() * WATER_PER_ENTITY_BLOCK);
            Vec3d startPos = this.getTopmostSeaPosForEntity(world, entity,
                    ModFluidTags.VERDANT_SPORES);
            SporeGrowthSpawner.spawnVerdantSporeGrowth(world, startPos,
                    LivingAetherSporeBlock.CATALYZE_VALUE, waterAmount, true, false, false);
        } else if (aetherSporeType == AetherSporeType.CRIMSON) {
            this.growCrimsonSpores(world, entity);

            // Only spawn spore growth if in the spore sea
            if (!world.getFluidState(entity.getBlockPos()).isIn(ModFluidTags.CRIMSON_SPORES)
                    || !((SporeFluidEntityStateAccess) entity).worldsinger$isTouchingSporeSea()) {
                return;
            }
            int waterAmount = MathHelper.ceil(
                    entity.getWidth() * entity.getHeight() * WATER_PER_ENTITY_BLOCK);
            Vec3d startPos = this.getTopmostSeaPosForEntity(world, entity,
                    ModFluidTags.CRIMSON_SPORES);
            // TODO: Spawn spore growth
        }
    }

    // Fill entity's bounding box with Verdant Vine Snare
    private void growVerdantSpores(World world, LivingEntity entity) {
        BlockState newBlockState = ModBlocks.VERDANT_VINE_SNARE.getDefaultState();

        for (BlockPos pos : BlockPosUtil.iterateBoundingBoxForEntity(entity)) {
            BlockState blockState = world.getBlockState(pos);
            if (blockState.isIn(ModBlockTags.SPORES_CAN_GROW)
                    && newBlockState.canPlaceAt(world, pos)) {
                world.setBlockState(pos, newBlockState);
            }
        }
    }

    // Fill entity's bounding box with Crimson Snare
    private void growCrimsonSpores(World world, LivingEntity entity) {
        BlockState newBlockState = ModBlocks.CRIMSON_SNARE.getDefaultState();

        for (BlockPos pos : BlockPosUtil.iterateBoundingBoxForEntity(entity)) {
            if (!newBlockState.canPlaceAt(world, pos)) {
                continue;
            }

            BlockState blockState = world.getBlockState(pos);
            if (blockState.isIn(ModBlockTags.SPORES_CAN_GROW)) {
                world.setBlockState(pos, newBlockState);
            } else if (blockState.isIn(ModBlockTags.SPORES_CAN_BREAK)) {
                SporeGrowthEntity.breakBlockFromSporeGrowth(world, pos, null);
                world.setBlockState(pos, newBlockState);
            }
        }
    }

    // Do a little hack to move spore growth position to the topmost block
    private Vec3d getTopmostSeaPosForEntity(World world, LivingEntity entity,
            TagKey<Fluid> fluidTag) {
        BlockPos.Mutable mutable = entity.getBlockPos().mutableCopy();

        do {
            mutable.move(Direction.UP);
        }
        while (world.getFluidState(mutable).isIn(fluidTag)
                && mutable.getY() < world.getTopY());

        if (world.getBlockState(mutable).isAir()) {
            // Found a good position, use it
            return mutable.move(Direction.DOWN).toCenterPos();
        } else {
            // Use original position
            return entity.getPos();
        }
    }

    public AetherSporeType getSporeType() {
        return aetherSporeType;
    }
}
