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
import net.minecraft.entity.player.PlayerEntity;
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

    private static final float CACHED_SIZE_PRECISION = 20.0f;
    private static final Int2ObjectMap<SporeDustParticleEffect> cachedDustParticleEffects = new Int2ObjectOpenHashMap<>();
    private static final int SPORE_EFFECT_DURATION_TICKS = 40;
    private static final Map<AetherSporeType, StatusEffect> SPORE_TO_STATUS_EFFECT = ImmutableMap.of(
            AetherSporeType.VERDANT, ModStatusEffects.VERDANT_SPORES);
    private static final float MIN_PARTICLE_SIZE = 0.3f;
    private static final float MAX_PARTICLE_SIZE = 10.0f;
    private static final double MIN_RADIUS = 0.1;
    private static final double MAX_RADIUS = 5.0;
    private static final double MIN_HEIGHT = 0.1;
    private static final double MAX_HEIGHT = 5.0;
    private static final double PARTICLE_VISUAL_HEIGHT_PENALTY = 0.1;

    private static final Random random = Random.create();

    // Create a cloud of spore particles at the given location
    // minY is the bottom of the particle cloud, not the center, for better ease of use.
    public static void createSporeParticles(ServerWorld world, AetherSporeType sporeType, double x,
            double minY, double z, double radius, double height, float particleSize,
            int particleCountPerBlock) {

        particleSize = Math.min(Math.max(particleSize, MIN_PARTICLE_SIZE), MAX_PARTICLE_SIZE);
        height = Math.min(Math.max(height, MIN_HEIGHT), MAX_HEIGHT);
        radius = Math.min(Math.max(radius, MIN_RADIUS), MAX_RADIUS);

        // Keep particle count consistent per block
        double volume = 4.0 * radius * radius * height;
        int particleCount = Math.max(particleCountPerBlock,
                MathHelper.ceil(particleCountPerBlock * volume));

        Constants.LOGGER.info(
                "Creating spore particle with radius=" + radius + ", height=" + height + ", size="
                        + particleSize + ", count=" + particleCount);

        double minX = x - radius;
        double minZ = z - radius;
        double maxX = x + radius;
        double maxY = minY + height;
        double maxZ = z + radius;

        if (SporeKillable.isSporeKillingBlockNearbyForRange(world, minX, minY,
                minZ, maxX, maxY, maxZ)) {
            sporeType = AetherSporeType.DEAD;
        }

        SporeParticleManager.spawnVisualSporeParticles(world, sporeType, x, minY, z,
                radius, height, particleSize, particleCount);
        SporeParticleManager.damageEntities(world, sporeType, minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static void createRandomSporeParticles(ServerWorld world, AetherSporeType sporeType,
            Vec3d pos, double baseRadius, double radiusDev, double baseHeight,
            double heightDev, float particleSize, int particleCountPerBlock) {
        double radius = baseRadius;
        double height = baseHeight;

        if (radiusDev > 0.0) {
            radius = baseRadius - radiusDev + random.nextDouble() * (2.0 * radiusDev);
        }
        if (heightDev > 0.0) {
            height = baseHeight - heightDev + random.nextDouble() * (2.0 * heightDev);
        }

        SporeParticleManager.createSporeParticles(world, sporeType, pos.getX(), pos.getY(),
                pos.getZ(), radius, height,
                particleSize, particleCountPerBlock);
    }

    public static void createRandomSporeParticlesForEntity(ServerWorld world,
            AetherSporeType sporeType, Entity entity, double radiusWidthMultiplier,
            double radiusDev, double heightMean, double heightDev,
            float particleSizeWidthMultiplier, int particleCountPerBlock) {
        float particleSize = entity.getWidth() * particleSizeWidthMultiplier;
        double radius = entity.getWidth() * radiusWidthMultiplier;
        SporeParticleManager.createRandomSporeParticles(world, sporeType, entity.getPos(), radius,
                radiusDev, heightMean, heightDev, particleSize, particleCountPerBlock);
    }


    // These are client-side and have no effect
    public static void spawnDisplayParticles(World world, AetherSporeType sporeType, double x,
            double y, double z, float particleSize) {
        if (SporeKillable.isSporeKillingBlockNearby(world, BlockPos.ofFloored(x, y, z))) {
            sporeType = AetherSporeType.DEAD;
        }

        ParticleEffect particleEffect = getCachedSporeParticleEffect(sporeType, particleSize);
        world.addParticle(particleEffect, x, y, z, 0.0, 0.0, 0.0);
    }

    public static void applySporeEffect(LivingEntity entity, StatusEffect statusEffect) {
        entity.addStatusEffect(new StatusEffectInstance(statusEffect,
                SporeParticleManager.SPORE_EFFECT_DURATION_TICKS, 0, true, false));
    }

    private static void spawnVisualSporeParticles(ServerWorld world,
            AetherSporeType sporeType,
            double x, double minY, double z, double radius, double height,
            float particleSize, int count) {
        // Spawn particle
        double deltaY;
        double y;
        if (height > PARTICLE_VISUAL_HEIGHT_PENALTY) {
            deltaY = (height - PARTICLE_VISUAL_HEIGHT_PENALTY) * 0.5;
            y = (minY + minY + deltaY) * 0.5;
        } else {
            deltaY = height * 0.5;
            y = minY + deltaY;
        }
        ParticleEffect particleEffect = getCachedSporeParticleEffect(sporeType, particleSize);
        world.spawnParticles(particleEffect, x, y, z, count, radius, deltaY, radius, 0.0);
    }

    private static void damageEntities(ServerWorld world, AetherSporeType sporeType,
            double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (sporeType == AetherSporeType.DEAD) {
            return;
        }

        StatusEffect statusEffect = SPORE_TO_STATUS_EFFECT.get(sporeType);
        if (statusEffect == null) {
            Constants.LOGGER.error("AetherSporeType does not have associated status effect: "
                    + sporeType.asString());
            return;
        }

        // Deal damage
        Box box = new Box(minX, minY, minZ, maxX, maxY, maxZ);
        List<LivingEntity> entitiesInRange = world.getEntitiesByClass(LivingEntity.class, box,
                entity -> true);
        for (LivingEntity entity : entitiesInRange) {
            if (entity instanceof PlayerEntity playerEntity && (playerEntity.isCreative()
                    || playerEntity.isSpectator())) {
                continue;
            }
            if (entity.getType().isIn(ModEntityTypeTags.SPORES_ALWAYS_AFFECT) || box.contains(
                    entity.getEyePos())) {
                applySporeEffect(entity, statusEffect);
            }
        }
    }

    private static SporeDustParticleEffect getCachedSporeParticleEffect(
            AetherSporeType sporeType, float size) {
        int key = hashTwoInts(sporeType.ordinal(),
                (int) Math.floor(size * CACHED_SIZE_PRECISION));
        return cachedDustParticleEffects.computeIfAbsent(key,
                k -> createDustParticleEffect(sporeType, size));
    }

    // https://stackoverflow.com/a/13871379
    private static int hashTwoInts(int a, int b) {
        return (a + b) * (a + b + 1) / 2 + a;
    }

    private static SporeDustParticleEffect createDustParticleEffect(AetherSporeType sporeType,
            float size) {
        // Make size follow the cached size precision to prevent unintentional imprecision
        size = ((int) (size * CACHED_SIZE_PRECISION)) / CACHED_SIZE_PRECISION;

        Constants.LOGGER.info(
                "Caching new dust particle effect (" + sporeType.asString() + ", " + size + ")");

        Vector3f particleColor = Vec3d.unpackRgb(sporeType.getParticleColor()).toVector3f();
        return new SporeDustParticleEffect(particleColor, size);
    }
}
