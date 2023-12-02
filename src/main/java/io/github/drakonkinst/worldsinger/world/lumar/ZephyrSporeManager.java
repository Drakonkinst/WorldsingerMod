package io.github.drakonkinst.worldsinger.world.lumar;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public final class ZephyrSporeManager {

    private static final float SPORE_TO_POWER_MULTIPLIER = 0.1f;

    public static void doZephyrSporeReaction(World world, Vec3d pos, int spores, int water,
            Random random) {
        // TODO: Deal damage
        // TODO: Drain surrounding zephyr spores too?
        float power = Math.min(spores, water) * SPORE_TO_POWER_MULTIPLIER + random.nextFloat();
        Worldsinger.LOGGER.info("spores = " + spores + ", water = " + water
                + " base_value = " + (Math.min(spores, water) * SPORE_TO_POWER_MULTIPLIER)
                + ", power = " + power);
        world.createExplosion(
                null,
                null,
                WindChargeEntity.EXPLOSION_BEHAVIOR,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                power,
                false,
                World.ExplosionSourceType.BLOW,
                ParticleTypes.GUST,
                ParticleTypes.GUST_EMITTER,
                ModSoundEvents.BLOCK_ZEPHYR_SEA_CATALYZE
        );
    }

    private ZephyrSporeManager() {}
}
