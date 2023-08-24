package io.github.drakonkinst.worldsinger.world.lumar;

import com.google.common.collect.ImmutableMap;
import io.github.drakonkinst.worldsinger.block.SporeKillable;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.particle.SporeDustParticleEffect;
import io.github.drakonkinst.worldsinger.util.Constants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
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

    public static boolean createSporeParticles(ServerWorld world, AetherSporeType aetherSporeType,
            double centerX, double bottomY,
            double centerZ, double horizontalRadius, double height, float particleSize, int count) {

        double centerY = bottomY + (height / 2.0f);
        double deltaY = height / 2.0f;

        int minX = (int) (centerX - horizontalRadius);
        int maxX = (int) (centerX + horizontalRadius);
        int minY = (int) bottomY;
        int maxY = (int) (bottomY + height);
        int minZ = (int) (centerZ - horizontalRadius);
        int maxZ = (int) (centerZ + horizontalRadius);

        if (SporeKillable.isSporeKillingBlockNearbyForRange(world, minX, minY, minZ, maxX, maxY,
                maxZ)) {
            aetherSporeType = AetherSporeType.DEAD;
        }

        spawnSporeParticles(world, aetherSporeType, centerX, centerY, centerZ, horizontalRadius,
                deltaY, particleSize, count);
        damageEntities(world, aetherSporeType, minX, minY, minZ, maxX, maxY, maxZ);
        return aetherSporeType == AetherSporeType.DEAD;
    }

    // These are client-side and have no effect
    public static void spawnSporeDisplayParticles(World world, AetherSporeType aetherSporeType,
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
            if (box.contains(entity.getEyePos())) {
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
