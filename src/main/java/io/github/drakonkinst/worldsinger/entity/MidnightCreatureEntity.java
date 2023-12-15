package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.particle.ModParticleTypes;
import io.github.drakonkinst.worldsinger.util.BoxUtil;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MidnightCreatureEntity extends ShapeshiftingEntity {

    private static final int IMITATE_NEAREST_INTERVAL = 20 * 5;
    private static final int AMBIENT_PARTICLE_INTERVAL = 10;
    private static final int NUM_DAMAGE_PARTICLES = 16;
    private static final String MORPHED_TRANSLATION_KEY = Util.createTranslationKey("entity",
            Worldsinger.id("midnight_creature.morphed"));

    public static DefaultAttributeContainer.Builder createMidnightCreatureAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    public MidnightCreatureEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(7, new WanderAroundGoal(this, 1.0));
    }

    @Override
    public void tick() {
        super.tick();

        // Mainly for testing. This behavior will likely need additional logic
        if (!this.getWorld().isClient() && !this.firstUpdate) {
            // Morphs should only occur from server side
            if (morph == null) {
                if (age % IMITATE_NEAREST_INTERVAL == 0) {
                    imitateNearestEntity();
                }
            }
        }

        if (!this.firstUpdate && this.getWorld().isClient()
                && this.age % AMBIENT_PARTICLE_INTERVAL == 0 && random.nextInt(3) != 0) {
            Vec3d pos = this.getRandomParticleSpawnPoint();
            this.getWorld()
                    .addParticle(ModParticleTypes.MIDNIGHT_ESSENCE, pos.getX(), pos.getY(),
                            pos.getZ(), 0.1 * random.nextGaussian(), 0.1 * random.nextGaussian(),
                            0.1 * random.nextGaussian());
        }
    }

    @Override
    public void onDamaged(DamageSource damageSource) {
        super.onDamaged(damageSource);
        for (int i = 0; i < NUM_DAMAGE_PARTICLES; ++i) {
            Vec3d pos = this.getRandomParticleSpawnPoint();
            this.getWorld()
                    .addParticle(ModParticleTypes.MIDNIGHT_ESSENCE, pos.getX(), pos.getY(),
                            pos.getZ(), 0.25 * random.nextGaussian(), 0.25 * random.nextGaussian(),
                            0.25 * random.nextGaussian());
        }
    }

    private Vec3d getRandomParticleSpawnPoint() {
        Box box = this.getBoundingBox();
        double x = box.minX + (box.maxX - box.minX) * random.nextDouble();
        double y = box.minY + (box.maxY - box.minY) * random.nextDouble();
        double z = box.minZ + (box.maxZ - box.minZ) * random.nextDouble();
        return new Vec3d(x, y, z);
    }

    private void imitateNearestEntity() {
        Vec3d pos = this.getPos();
        Box box = BoxUtil.createBoxAroundPos(pos, 16.0);

        // LivingEntity nearest = this.getWorld()
        //         .getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, this, pos.getX(),
        //                 pos.getY(), pos.getZ(), box);

        List<LivingEntity> candidates = this.getWorld()
                .getEntitiesByClass(LivingEntity.class, box, entity -> !entity.getType()
                        .isIn(ModEntityTypeTags.MIDNIGHT_CREATURES_CANNOT_IMITATE));
        if (candidates.isEmpty()) {
            return;
        }

        LivingEntity nearest = candidates.get(0);
        double minDistanceSq = Float.MAX_VALUE;

        for (LivingEntity candidate : candidates) {
            double distanceSq = candidate.getPos().squaredDistanceTo(this.getPos());
            if (distanceSq < minDistanceSq) {
                nearest = candidate;
                minDistanceSq = distanceSq;
            }
        }

        // Should always make a copy of the entity
        LivingEntity copy = (LivingEntity) nearest.getType().create(this.getWorld());
        // TODO: Make an exact copy? Can this be a method in ShapeshifterEntity or Shapeshifter?
        // TODO: Magma Cubes and Slimes currently randomize their size
        if (copy != null) {
            ((MidnightOverlayAccess) copy).worldsinger$setMidnightOverlay(true);
        }
        this.updateMorph(copy);
    }

    @Override
    public void onMorphEntitySpawn(LivingEntity morph) {
        super.onMorphEntitySpawn(morph);
        ((MidnightOverlayAccess) morph).worldsinger$setMidnightOverlay(true);
    }

    @Override
    protected Text getDefaultName() {
        if (morph != null) {
            return Text.translatable(MORPHED_TRANSLATION_KEY, morph.getName());
        }
        return super.getDefaultName();
    }
}
