package io.github.drakonkinst.worldsinger.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import io.github.drakonkinst.worldsinger.entity.MidnightCreatureEntity;
import io.github.drakonkinst.worldsinger.entity.ModEntityTypeTags;
import io.github.drakonkinst.worldsinger.util.EntityUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.util.BrainUtils;

public class MidnightCreatureImitation<E extends MidnightCreatureEntity> extends
        ExtendedBehaviour<E> {

    private static final List<Pair<MemoryModuleType<?>, MemoryModuleState>> MEMORY_REQUIREMENTS = ObjectArrayList.of(
            Pair.of(MemoryModuleType.MOBS, MemoryModuleState.VALUE_PRESENT));

    private static final int IMITATION_ATTEMPT_INTERVAL = 20;
    private static final double LOOK_NEAR_THRESHOLD = 1.0 - 0.025;
    private int imitationAttemptTicks = IMITATION_ATTEMPT_INTERVAL;
    private int imitationAttemptsRemaining = 6;

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean shouldRun(ServerWorld level, E entity) {
        return entity.getMorph() == null;
    }

    @Override
    protected void tick(E entity) {
        ++imitationAttemptTicks;
        if (imitationAttemptTicks >= IMITATION_ATTEMPT_INTERVAL) {
            imitationAttemptTicks = 0;
            BrainUtils.withMemory(entity, MemoryModuleType.MOBS, nearbyEntities -> {
                attemptImitation(entity, nearbyEntities);
            });
        }
    }

    @Override
    protected boolean shouldKeepRunning(E entity) {
        return entity.getMorph() == null;
    }

    // Midnight Essence required directly ties to their bounding box, rounded to the nearest block
    private int getMidnightEssenceRequired(LivingEntity entity) {
        int width = MathHelper.ceil(entity.getWidth());
        int height = MathHelper.ceil(entity.getHeight());
        return width * width * height;
    }

    private void attemptImitation(E midnightCreature, List<LivingEntity> nearbyEntities) {
        // TODO: Can we get a count of how many untransformed midnight mobs there are here, as well as nearby blocks?
        PlayerEntity controller = midnightCreature.getController();
        if (controller != null) {
            // See if the player is looking at a mob for the entity to imitate
            // Uses logic similar to an Enderman
            Vec3d lookDir = EntityUtil.getLookRotationVector(controller).normalize();
            Vec3d controllerPos = controller.getEyePos();
            LivingEntity closestEntity = null;
            double closestEntityDistanceSq = Double.MAX_VALUE;

            for (LivingEntity entity : nearbyEntities) {
                if (!entity.getType().isIn(ModEntityTypeTags.MIDNIGHT_CREATURES_CANNOT_IMITATE)) {
                    continue;
                }
                Vec3d entityPos = EntityUtil.getCenterPos(entity);
                Vec3d delta = entityPos.subtract(controllerPos);
                double distanceToEntity = delta.length();
                double dotProduct = lookDir.dotProduct(delta.normalize());
                if (dotProduct > LOOK_NEAR_THRESHOLD / distanceToEntity) {
                    // Can consider as a candidate
                    double distSqFromLine = controllerPos.crossProduct(delta).lengthSquared();
                    if (distSqFromLine < closestEntityDistanceSq) {
                        closestEntity = entity;
                        closestEntityDistanceSq = distSqFromLine;
                    }
                }
            }

            if (closestEntity != null) {
                closestEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20));
                // TODO: Attempt to transform into target

                if (midnightCreature.getMorph() != null) {
                    return;
                }
            }

        }

        if (imitationAttemptsRemaining > 0) {
            --imitationAttemptsRemaining;
        }

        if (imitationAttemptsRemaining <= 0) {
            // Just imitate nearest that it can imitate
        }
    }
}
