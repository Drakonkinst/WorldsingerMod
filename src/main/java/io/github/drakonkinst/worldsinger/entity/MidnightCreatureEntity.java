package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.particle.ModParticleTypes;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import io.github.drakonkinst.worldsinger.util.BoxUtil;
import io.github.drakonkinst.worldsinger.util.EntityUtil;
import io.github.drakonkinst.worldsinger.util.ModEnums.PathNodeType;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class MidnightCreatureEntity extends ShapeshiftingEntity {

    private static final int IMITATE_NEAREST_INTERVAL = 20 * 5;
    private static final int AMBIENT_PARTICLE_INTERVAL = 10;
    private static final int NUM_DAMAGE_PARTICLES = 16;
    private static final int NUM_TRANSFORM_PARTICLES = 32;
    private static final String MORPHED_NAME_TRANSLATION_KEY = Util.createTranslationKey("entity",
            Worldsinger.id("midnight_creature.morphed"));
    private static final Set<StatusEffect> IMMUNE_TO = Set.of(StatusEffects.WITHER,
            StatusEffects.POISON, StatusEffects.HUNGER, ModStatusEffects.CRIMSON_SPORES,
            ModStatusEffects.MIDNIGHT_SPORES, ModStatusEffects.ROSEITE_SPORES,
            ModStatusEffects.SUNLIGHT_SPORES, ModStatusEffects.VERDANT_SPORES,
            ModStatusEffects.ZEPHYR_SPORES);

    public static DefaultAttributeContainer.Builder createMidnightCreatureAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    public static void spawnMidnightParticle(World world, Entity entity, Random random,
            double velocity) {
        Vec3d pos = EntityUtil.getRandomPointInBoundingBox(entity, random);
        double velocityX = velocity * random.nextGaussian();
        double velocityY = velocity * random.nextGaussian();
        double velocityZ = velocity * random.nextGaussian();
        world.addParticle(ModParticleTypes.MIDNIGHT_ESSENCE, pos.getX(), pos.getY(), pos.getZ(),
                velocityX, velocityY, velocityZ);
    }

    public static void spawnMidnightParticles(World world, Entity entity, Random random,
            double velocity, int count) {
        for (int i = 0; i < count; ++i) {
            MidnightCreatureEntity.spawnMidnightParticle(world, entity, random, velocity);
        }
    }

    public MidnightCreatureEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;

        // Set to same penalty as water
        this.setPathfindingPenalty(PathNodeType.AETHER_SPORE_SEA, 8.0F);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(3, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(3, new RevengeGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        // Only PlayerEntity and HostileEntity tick hand swing by default, so add it here too
        this.tickHandSwing();

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
            MidnightCreatureEntity.spawnMidnightParticle(this.getWorld(), this, random, 0.1);
        }
    }

    @Override
    public void onDamaged(DamageSource damageSource) {
        super.onDamaged(damageSource);
        MidnightCreatureEntity.spawnMidnightParticles(this.getWorld(), this, random, 0.25,
                MidnightCreatureEntity.NUM_DAMAGE_PARTICLES);
    }

    public boolean canPickUpLoot() {
        return false;
    }

    private void imitateNearestEntity() {
        LivingEntity nearest = getNearestEntityToImitate();
        if (nearest != null) {
            Shapeshifter.createMorphFromEntity(this, nearest, true);
        }
    }

    private LivingEntity getNearestEntityToImitate() {
        Vec3d pos = this.getPos();
        Box box = BoxUtil.createBoxAroundPos(pos, 16.0);

        // LivingEntity nearest = this.getWorld()
        //         .getClosestEntity(LivingEntity.class, TargetPredicate.DEFAULT, this, pos.getX(),
        //                 pos.getY(), pos.getZ(), box);

        List<LivingEntity> candidates = this.getWorld()
                .getEntitiesByClass(LivingEntity.class, box, entity -> !entity.getType()
                        .isIn(ModEntityTypeTags.MIDNIGHT_CREATURES_CANNOT_IMITATE));
        if (candidates.isEmpty()) {
            return null;
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
        return nearest;
    }

    @Override
    public void onMorphEntitySpawn(LivingEntity morph) {
        super.onMorphEntitySpawn(morph);
        ((MidnightOverlayAccess) morph).worldsinger$setMidnightOverlay(true);
    }

    @Override
    public void afterMorphEntitySpawn(LivingEntity morph, boolean showTransformEffects) {
        super.afterMorphEntitySpawn(morph, showTransformEffects);
        if (showTransformEffects && this.getWorld().isClient()) {
            MidnightCreatureEntity.spawnMidnightParticles(this.getWorld(), this, random, 0.2,
                    NUM_TRANSFORM_PARTICLES);
            // TODO: This doesn't actually make any sound
            this.playSound(ModSoundEvents.ENTITY_MIDNIGHT_CREATURE_TRANSFORM, 1.0f, 1.0f);
        }
    }

    // Hacky way of disabling certain status effects. Clashes a bit with the entity tag for spore effects, etc.
    // TODO: Investigate better solutions
    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        StatusEffect effectType = effect.getEffectType();
        if (IMMUNE_TO.contains(effectType)) {
            return false;
        }
        return super.canHaveStatusEffect(effect);
    }

    @Override
    protected Text getDefaultName() {
        if (morph != null) {
            return Text.translatable(MORPHED_NAME_TRANSLATION_KEY, morph.getName());
        }
        return super.getDefaultName();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.ENTITY_MIDNIGHT_CREATURE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ENTITY_MIDNIGHT_CREATURE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENTITY_MIDNIGHT_CREATURE_DEATH;
    }

    /* Should act like a hostile mob though it does not extend HostileEntity */

    @Override
    protected boolean isDisallowedInPeaceful() {
        return true;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_HOSTILE_SPLASH;
    }

    @Override
    public LivingEntity.FallSounds getFallSounds() {
        return new LivingEntity.FallSounds(SoundEvents.ENTITY_HOSTILE_SMALL_FALL,
                SoundEvents.ENTITY_HOSTILE_BIG_FALL);
    }

    @Override
    public boolean shouldDropXp() {
        return true;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }
}
