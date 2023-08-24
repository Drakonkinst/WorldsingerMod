package io.github.drakonkinst.worldsinger.world.lumar;

import com.google.common.collect.ImmutableMap;
import io.github.drakonkinst.worldsinger.block.SporeKillable;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypeTags;
import io.github.drakonkinst.worldsinger.particle.SporeDustParticleEffect;
import io.github.drakonkinst.worldsinger.util.Constants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Vector3f;

// Represents a mote of spore particles, which can
// actively damage entities it comes into contact with.
public final class SporeParticleManager {

    private SporeParticleManager() {}

    private static final float CACHED_SIZE_PRECISION = 10.0f;
    private static final Int2ObjectMap<SporeDustParticleEffect> cachedDustParticleEffects = new Int2ObjectOpenHashMap<>();
    private static final int SPORE_EFFECT_DURATION_TICKS = 40;
    private static final Map<AetherSporeType, StatusEffect> SPORE_TO_STATUS_EFFECT = ImmutableMap.of(
            AetherSporeType.VERDANT, ModStatusEffects.VERDANT_SPORES);

    // Splashing when landing on block
    private static final double SPLASH_MIN_HEIGHT = 0.6;
    private static final double SPLASH_HEIGHT_GAIN_PER_BLOCK = 0.05;
    private static final double SPLASH_MAX_HEIGHT = 4.0;
    private static final double SPLASH_SPRINTING_MULTIPLIER = 4.0 / 3.0;
    private static final double SPLASH_SNEAKING_MULTIPLIER = 0.5;
    private static final double SPLASH_RADIUS_MULTIPLIER = 0.75;
    private static final int SPLASH_PARTICLES_PER_BLOCK_HEIGHT = 10;

    // Footsteps when walking or sprinting on block
    private static final double FOOTSTEP_MIN_HEIGHT = 0.25;
    private static final double FOOTSTEP_RADIUS_MULTIPLIER = 0.5;
    private static final double FOOTSTEP_SPRINTING_MULTIPLIER = 1.5;
    private static final double FOOTSTEP_HEIGHT_MULTIPLIER = 0.5;
    private static final float FOOTSTEP_PARTICLE_SIZE = 0.75f;
    private static final int FOOTSTEP_PARTICLES = 5;

    private static final Random random = Random.create();

    public static boolean createSporeParticles(ServerWorld world, AetherSporeType aetherSporeType,
            double centerX, double bottomY,
            double centerZ, double horizontalRadius, double height, float particleSize, int count) {

        double centerY = bottomY + (height / 2.0f);
        double deltaY = height / 2.0f;

        Constants.LOGGER.info("HEIGHT: " + height);

        double minX = centerX - horizontalRadius;
        double minY = bottomY;
        double minZ = centerZ - horizontalRadius;
        double maxX = centerX + horizontalRadius;
        double maxY = bottomY + height;
        double maxZ = centerZ + horizontalRadius;

        int searchMinX = MathHelper.floor(minX);
        int searchMinY = MathHelper.floor(minY);
        int searchMinZ = MathHelper.floor(minZ);
        int searchMaxX = MathHelper.ceil(maxX);
        int searchMaxY = MathHelper.ceil(maxY);
        int searchMaxZ = MathHelper.ceil(maxZ);

        if (SporeKillable.isSporeKillingBlockNearbyForRange(world, searchMinX, searchMinY,
                searchMinZ, searchMaxX, searchMaxY,
                searchMaxZ)) {
            aetherSporeType = AetherSporeType.DEAD;
        }

        spawnSporeParticles(world, aetherSporeType, centerX, centerY, centerZ, horizontalRadius,
                deltaY, particleSize, count);
        damageEntities(world, aetherSporeType, minX, minY, minZ, maxX, maxY, maxZ);
        return aetherSporeType == AetherSporeType.DEAD;
    }

    public static void spawnLandingParticles(ServerWorld world,
            AetherSporeType aetherSporeType, Entity entity, float fallDistance) {
        // TODO: Make this based on entity weight later
        float particleSize = entity.getWidth();
        Vec3d entityPos = entity.getPos();

        double radius = entity.getWidth() * SPLASH_RADIUS_MULTIPLIER;
        double multiplier = 1.0;
        if (entity.isSneaking()) {
            multiplier = SPLASH_SNEAKING_MULTIPLIER;
        } else if (entity.isSprinting()) {
            multiplier = SPLASH_SPRINTING_MULTIPLIER;
        }

        double height = SPLASH_MIN_HEIGHT + fallDistance * SPLASH_HEIGHT_GAIN_PER_BLOCK + (
                random.nextFloat() * multiplier);
        height = Math.min(height, SPLASH_MAX_HEIGHT);
        int count = SPLASH_PARTICLES_PER_BLOCK_HEIGHT * MathHelper.ceil(height);

        SporeParticleManager.createSporeParticles(world, aetherSporeType, entityPos.getX(),
                entityPos.getY(), entityPos.getZ(), radius, height, particleSize,
                count);
    }

    public static void spawnFootstepParticles(ServerWorld world, AetherSporeType aetherSporeType,
            Entity entity) {
        Vec3d entityPos = entity.getPos();
        double radius = entity.getWidth() * FOOTSTEP_RADIUS_MULTIPLIER;
        double multiplier =
                entity.isSprinting() ? FOOTSTEP_SPRINTING_MULTIPLIER : FOOTSTEP_HEIGHT_MULTIPLIER;
        double height =
                FOOTSTEP_MIN_HEIGHT + random.nextFloat() * multiplier;
        SporeParticleManager.createSporeParticles(world, aetherSporeType, entityPos.getX(),
                entityPos.getY(), entityPos.getZ(), radius, height, FOOTSTEP_PARTICLE_SIZE,
                FOOTSTEP_PARTICLES);
    }

    // These are client-side and have no effect
    public static void spawnDisplayParticles(World world, AetherSporeType aetherSporeType,
            double x, double y, double z, float particleSize) {
        if (SporeKillable.isSporeKillingBlockNearby(world, BlockPos.ofFloored(x, y, z))) {
            aetherSporeType = AetherSporeType.DEAD;
        }

        ParticleEffect particleEffect = getCachedSporeParticleEffect(aetherSporeType, particleSize);
        world.addParticle(particleEffect, x, y, z, 0.0, 0.0, 0.0);
    }

    public static void applySporeEffect(LivingEntity entity, StatusEffect statusEffect) {
        entity.addStatusEffect(new StatusEffectInstance(statusEffect,
                SporeParticleManager.SPORE_EFFECT_DURATION_TICKS, 0, true, false));
    }

    private static void spawnSporeParticles(ServerWorld world, AetherSporeType aetherSporeType,
            double centerX, double centerY, double centerZ, double horizontalRadius, double deltaY,
            float particleSize, int count) {
        // Spawn particle
        ParticleEffect particleEffect = getCachedSporeParticleEffect(aetherSporeType, particleSize);
        world.spawnParticles(particleEffect, centerX,
                centerY, centerZ,
                count, horizontalRadius,
                deltaY, horizontalRadius, 0.0);
    }

    private static void damageEntities(ServerWorld world, AetherSporeType aetherSporeType,
            double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (aetherSporeType == AetherSporeType.DEAD) {
            return;
        }

        StatusEffect statusEffect = SPORE_TO_STATUS_EFFECT.get(aetherSporeType);
        if (statusEffect == null) {
            Constants.LOGGER.error("AetherSporeType does not have associated status effect: "
                    + aetherSporeType.asString());
            return;
        }

        // Deal damage
        Box box = new Box(minX, minY, minZ, maxX, maxY, maxZ);
        List<LivingEntity> entitiesInRange = world.getEntitiesByClass(LivingEntity.class, box,
                entity -> true);
        for (LivingEntity entity : entitiesInRange) {
            if (entity.getType().isIn(ModEntityTypeTags.SPORES_ALWAYS_AFFECT) || box.contains(
                    entity.getEyePos())) {
                applySporeEffect(entity, statusEffect);
            }
        }
    }

    private static SporeDustParticleEffect getCachedSporeParticleEffect(
            AetherSporeType aetherSporeType, float size) {
        int key = hashTwoInts(aetherSporeType.ordinal(),
                (int) Math.floor(size * CACHED_SIZE_PRECISION));
        return cachedDustParticleEffects.computeIfAbsent(key,
                k -> createDustParticleEffect(aetherSporeType, size));
    }

    // https://stackoverflow.com/a/13871379
    private static int hashTwoInts(int a, int b) {
        return (a + b) * (a + b + 1) / 2 + a;
    }

    private static SporeDustParticleEffect createDustParticleEffect(
            AetherSporeType aetherSporeType,
            float size) {
        Constants.LOGGER.info(
                "Caching new dust particle effect (" + aetherSporeType.asString() + ", " + size
                        + ")");

        // Make size follow the cached size precision to prevent unintentional imprecision
        size = ((int) (size * CACHED_SIZE_PRECISION)) / CACHED_SIZE_PRECISION;

        Vector3f particleColor = Vec3d.unpackRgb(aetherSporeType.getParticleColor()).toVector3f();
        return new SporeDustParticleEffect(particleColor, size);
    }
}
