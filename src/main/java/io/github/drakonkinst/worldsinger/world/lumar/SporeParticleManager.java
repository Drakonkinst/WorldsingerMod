package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.block.SporeKillable;
import io.github.drakonkinst.worldsinger.util.Constants;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3f;

// Represents a mote of spore particles, which can
// actively damage entities it comes into contact with.
public final class SporeParticleManager {

    private SporeParticleManager() {}

    private static final float CACHED_SIZE_PRECISION = 10.0f;
    private static final Int2ObjectMap<DustParticleEffect> cachedDustParticleEffects = new Int2ObjectOpenHashMap<>();

    public static boolean spawnSporeParticle(ServerWorld world, AetherSporeType aetherSporeType,
            float centerX, float bottomY,
            float centerZ, float horizontalRadius, float height, float particleSize, int count) {

        float centerY = bottomY + (height / 2.0f);
        float deltaY = height / 2.0f;

        if (SporeParticleManager.isSporeKilled(world, centerX, bottomY, centerZ, horizontalRadius,
                height)) {
            aetherSporeType = AetherSporeType.DEAD;
        }

        ParticleEffect particleEffect = getCachedDustParticleEffect(aetherSporeType, particleSize);
        world.spawnParticles(particleEffect, centerX,
                centerY, centerZ,
                count, horizontalRadius,
                deltaY, horizontalRadius, 0.0);
        return aetherSporeType == AetherSporeType.DEAD;
    }

    // This is thorough for now, but might be easier to do with a single block check
    private static boolean isSporeKilled(ServerWorld world, float centerX, float bottomY,
            float centerZ, float horizontalRadius, float height) {
        int minX = (int) (centerX - horizontalRadius);
        int maxX = (int) (centerX + horizontalRadius);
        int minY = (int) bottomY;
        int maxY = (int) (bottomY + height);
        int minZ = (int) (centerZ - horizontalRadius);
        int maxZ = (int) (centerZ + horizontalRadius);

        return SporeKillable.isSporeKillingBlockNearbyForRange(world, minX, minY, minZ, maxX, maxY,
                maxZ);
    }

    private static DustParticleEffect getCachedDustParticleEffect(AetherSporeType aetherSporeType,
            float size) {
        int key = hashTwoInts(aetherSporeType.ordinal(),
                (int) Math.floor(size * CACHED_SIZE_PRECISION));
        return cachedDustParticleEffects.computeIfAbsent(key,
                k -> createDustParticleEffect(aetherSporeType, size));
    }

    // https://stackoverflow.com/a/13871379
    private static int hashTwoInts(int a, int b) {
        return (a + b) * (a + b + 1) / 2 + a;
    }

    private static DustParticleEffect createDustParticleEffect(AetherSporeType aetherSporeType,
            float size) {
        Constants.LOGGER.info(
                "Caching new dust particle effect (" + aetherSporeType.asString() + ", " + size
                        + ")");

        // Make size follow the cached size precision to prevent unintentional imprecision
        size = ((int) (size * CACHED_SIZE_PRECISION)) / CACHED_SIZE_PRECISION;

        int particleColor = aetherSporeType.getParticleColor();
        float red = AetherSporeType.getNormalizedRed(particleColor);
        float green = AetherSporeType.getNormalizedGreen(particleColor);
        float blue = AetherSporeType.getNormalizedBlue(particleColor);
        return new DustParticleEffect(new Vector3f(red, green, blue), size);
    }
}
