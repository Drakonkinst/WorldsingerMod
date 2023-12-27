package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.block.ModBlockTags;
import io.github.drakonkinst.worldsinger.component.MidnightAetherBondComponent;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.ThirstManagerComponent;
import io.github.drakonkinst.worldsinger.cosmere.ShapeshiftingManager;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.entity.ai.behavior.OptionalAttackTarget;
import io.github.drakonkinst.worldsinger.entity.ai.behavior.StudyTarget;
import io.github.drakonkinst.worldsinger.entity.ai.sensor.NearbyRepellentSensor;
import io.github.drakonkinst.worldsinger.entity.ai.sensor.NearestAttackableSensor;
import io.github.drakonkinst.worldsinger.entity.data.MidnightOverlayAccess;
import io.github.drakonkinst.worldsinger.mixin.accessor.EntityAccessor;
import io.github.drakonkinst.worldsinger.particle.ModParticleTypes;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import io.github.drakonkinst.worldsinger.util.BoxUtil;
import io.github.drakonkinst.worldsinger.util.EntityUtil;
import io.github.drakonkinst.worldsinger.util.ModEnums.PathNodeType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowEntity;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRetaliateTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.SensoryUtils;
import org.jetbrains.annotations.Nullable;

public class MidnightCreatureEntity extends ShapeshiftingEntity implements
        SmartBrainOwner<MidnightCreatureEntity>, Controllable, Monster {

    // NBT
    public static final String MIDNIGHT_ESSENCE_AMOUNT_KEY = "MidnightEssenceAmount";
    public static final String CONTROLLER_KEY = "Controller";
    // Tracked Data
    private static final TrackedData<Optional<UUID>> CONTROLLER_UUID = DataTracker.registerData(
            MidnightCreatureEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final String MORPHED_NAME_TRANSLATION_KEY = Util.createTranslationKey("entity",
            Worldsinger.id("midnight_creature.morphed"));

    // Behavior
    private static final int IMITATE_NEAREST_INTERVAL = 20 * 5;
    private static final int DRAIN_INTERVAL_TICKS = 20 * 3;
    private static final int ANGER_TIME = 20 * 30;
    private static final float SPRINTING_MULTIPLIER = 1.4f;
    private static final Set<RegistryEntry<StatusEffect>> IMMUNE_TO = Set.of(StatusEffects.WITHER,
            StatusEffects.POISON, StatusEffects.HUNGER, ModStatusEffects.CRIMSON_SPORES,
            ModStatusEffects.MIDNIGHT_SPORES, ModStatusEffects.ROSEITE_SPORES,
            ModStatusEffects.SUNLIGHT_SPORES, ModStatusEffects.VERDANT_SPORES,
            ModStatusEffects.ZEPHYR_SPORES);

    // Attributes
    private static final double DEFAULT_MOVEMENT_SPEED = 0.25;
    private static final double DEFAULT_MAX_HEALTH = 20.0;
    private static final double MAX_HEALTH_SIZE_MULTIPLIER = 17;
    private static final double ATTACK_DAMAGE_SIZE_MULTIPLIER = 2.5;
    private static final float MIN_MAX_HEALTH = 8.0f;       // Same as Silverfish
    private static final float MAX_MAX_HEALTH = 100.0f;     // Same as Ravager
    private static final float MIN_ATTACK_DAMAGE = 3.0f;    // Same as Zombie
    private static final float MAX_ATTACK_DAMAGE = 12.0f;   // Same as Ravager

    // Particles
    private static final int AMBIENT_PARTICLE_INTERVAL = 10;
    private static final int NUM_DAMAGE_PARTICLES = 16;
    private static final int NUM_TRANSFORM_PARTICLES = 32;
    private static final int NUM_TRAIL_PARTICLES = 16;
    private static final float TRAIL_PARTICLE_SPEED = 0.1f;
    private static final float MOUTH_OFFSET = -0.2f;

    public static DefaultAttributeContainer.Builder createMidnightCreatureAttributes() {
        // Before transforming, should not be able to move or attack
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, DEFAULT_MAX_HEALTH);
    }

    public static double getMaxHealthForVolume(float volume) {
        double value = volume * MAX_HEALTH_SIZE_MULTIPLIER;
        return MathHelper.clamp(Math.round(value), MIN_MAX_HEALTH, MAX_MAX_HEALTH);
    }

    public static double getAttackDamageForVolume(float volume) {
        double value = volume * ATTACK_DAMAGE_SIZE_MULTIPLIER;
        return MathHelper.clamp(Math.round(value), MIN_ATTACK_DAMAGE, MAX_ATTACK_DAMAGE);
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

    private final Object2IntMap<UUID> waterBribes = new Object2IntOpenHashMap<>();
    private ControlLevel controlLevel = ControlLevel.OUT_OF_CONTROL;
    private int midnightEssenceAmount = 0;
    private int drainIntervalTicks = 0;
    @Nullable
    private PlayerEntity controller;

    public MidnightCreatureEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;

        // Allow it to swim
        this.getNavigation().setCanSwim(true);

        // Set to same penalty as water
        this.setPathfindingPenalty(PathNodeType.AETHER_SPORE_SEA, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_SILVER, 8.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_SILVER, -1.0F);
    }

    public MidnightCreatureEntity(World world) {
        this(ModEntityTypes.MIDNIGHT_CREATURE, world);
    }

    // Data Tracker
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CONTROLLER_UUID, Optional.empty());
    }

    @Override
    public void setControllerUuid(UUID uuid) {
        if (uuid == null) {
            this.dataTracker.set(CONTROLLER_UUID, Optional.empty());
        } else {
            this.dataTracker.set(CONTROLLER_UUID, Optional.of(uuid));
        }
    }

    @Override
    public PlayerEntity getController() {
        UUID controllerUUID = getControllerUuid();
        if (controller == null && controllerUUID != null) {
            controller = this.getWorld().getPlayerByUuid(controllerUUID);
        }
        if (controller != null && controller.isRemoved()) {
            controller = null;
        }
        if (controller != null && controllerUUID == null) {
            controller = null;
        }
        return controller;
    }

    @Override
    public UUID getControllerUuid() {
        return this.dataTracker.get(CONTROLLER_UUID).orElse(null);
    }

    // AI
    // To avoid creating new memory module types, we take advantage of existing ones
    // UNIVERSAL_ANGER is used when the mob has been provoked and should not stop to study targets

    @Override
    protected Brain.Profile<?> createBrainProfile() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    protected void mobTick() {
        this.tickBrain(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends MidnightCreatureEntity>> getSensors() {
        return ObjectArrayList.of(
                // Keep track of all nearby entities (used to pick a transform target)
                new NearbyLivingEntitySensor<>(),
                // Filter NEAREST_ATTACKABLE from tracked nearby entities
                new NearestAttackableSensor<MidnightCreatureEntity>().setPredicate(
                        (target, entity) -> {
                            if (target.getType().isIn(ModEntityTypeTags.SPORES_NEVER_AFFECT)) {
                                return false;
                            }
                            if (target.getUuid().equals(entity.getControllerUuid())) {
                                return false;
                            }
                            return SensoryUtils.isEntityAttackable(entity, target);
                        }),
                // Track who hurt the mob to retaliate
                new HurtBySensor<>()
    }

    @Override
    public BrainActivityGroup<? extends MidnightCreatureEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(new FloatToSurfaceOfFluid<>(), new LookAtTarget<>(),
                new FollowEntity<MidnightCreatureEntity, LivingEntity>().following(
                                MidnightCreatureEntity::getController)
                        // Does not have as strict of a follow distance.
                        .stopFollowingWithin(8.0f).speedMod(SPRINTING_MULTIPLIER),
                new MoveToWalkTarget<>());
    }

    @SuppressWarnings("unchecked")
    @Override
    public BrainActivityGroup<? extends MidnightCreatureEntity> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                // Set attack target
                new FirstApplicableBehaviour<>(
                        // Target the controller's attacker or target if controlled
                        new OptionalAttackTarget<MidnightCreatureEntity>(false).targetFinder(
                                entity -> {
                                    PlayerEntity controller = entity.getController();
                                    if (controller == null) {
                                        return null;
                                    }
                                    LivingEntity attacker = controller.getAttacker();
                                    if (attacker != null && canAttackWithController(attacker,
                                            controller)) {
                                        return attacker;
                                    }
                                    LivingEntity attacking = controller.getAttacking();
                                    if (attacking != null && canAttackWithController(attacking,
                                            controller)) {
                                        return attacking;
                                    }
                                    return null;
                                }).attackPredicate(entity -> entity.getControllerUuid() != null),
                        // Retaliate against attackers
                        new SetRetaliateTarget<MidnightCreatureEntity>().alertAlliesWhen(
                                (owner, attacker) -> true).isAllyIf((owner, ally) -> {
                            if (!(ally instanceof MidnightCreatureEntity midnightAlly)) {
                                return false;
                            }

                            // Not an ally if controlled by another player
                            if (midnightAlly.getControllerUuid() != null
                                    && midnightAlly.getControllerUuid()
                                    != owner.getControllerUuid()) {
                                return false;
                            }

                            // Not an ally if already targeting
                            Entity lastHurtBy = BrainUtils.getMemory(ally,
                                    MemoryModuleType.HURT_BY_ENTITY);
                            return lastHurtBy == null || !ally.isTeammate(lastHurtBy);
                        }),
                        // Attack NEAREST_ATTACKABLE if uncontrolled
                        new SetAttackTarget<MidnightCreatureEntity>().attackPredicate(
                                entity -> entity.getControllerUuid() == null),
                        // Look at a player
                        new SetPlayerLookTarget<>(),
                        // Look somewhere random
                        new SetRandomLookTarget<>()),
                new OneRandomBehaviour<>(new SetRandomWalkTarget<>(),
                        new Idle<>().runFor(entity -> entity.getRandom().nextBetween(30, 60))));
    }

    @SuppressWarnings("unchecked")
    @Override
    public BrainActivityGroup<? extends MidnightCreatureEntity> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                // Invalidate target if they become the mob's controller or become untargetable
                new InvalidateAttackTarget<MidnightCreatureEntity>().invalidateIf(
                        (entity, target) -> {
                            if (target instanceof PlayerEntity player) {
                                if (player.isCreative() || player.isSpectator()) {
                                    return true;
                                }
                                return player.getUuid().equals(entity.getControllerUuid());
                            }
                            return false;
                        }),
                // Sprint towards target
                new SetWalkTargetToAttackTarget<>().speedMod(
                        (owner, target) -> SPRINTING_MULTIPLIER),
                // Begin attack
                new FirstApplicableBehaviour<>(
                        // If not aggro-ed and target is holding water, study them
                        new StudyTarget<MidnightCreatureEntity>(100).canStudy((entity, target) -> {
                            if (entity.getControllerUuid() != null) {
                                return false;
                            }
                            // TODO Make more robust
                            return target.getMainHandStack().isOf(Items.WATER_BUCKET)
                                    || target.getOffHandStack().isOf(Items.WATER_BUCKET);
                        }).whenStopping(entity -> {
                            BrainUtils.setForgettableMemory(entity,
                                    MemoryModuleType.UNIVERSAL_ANGER, true, ANGER_TIME);
                        }),
                        // Start attacking
                        new AnimatableMeleeAttack<MidnightCreatureEntity>(0)));
    }

    private boolean canAttackWithController(LivingEntity target, LivingEntity controller) {
        // Stop hitting yourself and never hit the controller
        if (this.equals(target) || controller.equals(target)) {
            return false;
        }

        // Like other mobs, never attack Creeper
        // Don't bother attacking Ghast since it flies
        if (target instanceof CreeperEntity || target instanceof GhastEntity) {
            return false;
        }

        // Don't hit animals tamed by the controller
        if (target instanceof TameableEntity tamedEntity) {
            return !tamedEntity.isTamed() || !controller.equals(tamedEntity.getOwner());
        }

        // Don't hit other mobs controlled by the controller
        if (target instanceof Controllable controllable) {
            PlayerEntity otherController = controllable.getController();
            return otherController == null || !otherController.equals(controller);
        }

        // Don't hit players on the same team
        if (target instanceof PlayerEntity playerTarget
                && controller instanceof PlayerEntity playerController
                && !playerController.shouldDamagePlayer(playerTarget)) {
            return false;
        }

        return true;
    }

    // Tick
    @Override
    public void tick() {
        super.tick();

        // Only PlayerEntity and HostileEntity tick hand swing by default, so add it here too
        this.tickHandSwing();

        World world = this.getWorld();

        // Mainly for testing. This behavior will likely need additional logic
        if (!world.isClient() && !this.firstUpdate) {
            // Morphs should only occur from server side
            if (morph == null) {
                if (age % IMITATE_NEAREST_INTERVAL == 0) {
                    imitateNearestEntity();
                }
            }
        }

        if (!world.isClient()) {
            UUID controllerUuid = getControllerUuid();
            if (controllerUuid != null) {
                ++drainIntervalTicks;
                if (drainIntervalTicks >= DRAIN_INTERVAL_TICKS) {
                    PlayerEntity controller = getController();
                    if (controller == null) {
                        resetController();
                    } else {
                        drainWaterFromHost(controller);
                    }
                    drainIntervalTicks = 0;
                }
            }
        }

        if (world.isClient() && !this.firstUpdate) {
            tickParticleEffects();
        }
    }

    private void tickParticleEffects() {
        // Ambient particles
        if (this.age % AMBIENT_PARTICLE_INTERVAL == 0 && random.nextInt(3) != 0) {
            MidnightCreatureEntity.spawnMidnightParticle(this.getWorld(), this, random, 0.1);
        }

        // Update client controller
        PlayerEntity controller = getController();

        if (controller != null) {
            Vec3d start = controller.getEyePos().add(0.0, MOUTH_OFFSET, 0.0);
            Vec3d destination = this.getPos().add(0.0, this.getHeight() / 2.0, 0.0f);
            Vec3d direction = destination.subtract(start).normalize();
            addTrailParticle(start, destination, 0, direction);
            addTrailParticle(start, destination, NUM_TRAIL_PARTICLES / 2, direction);
        }
    }

    private void addTrailParticle(Vec3d start, Vec3d destination, int offset, Vec3d direction) {
        double delta = (double) ((this.age + offset) % NUM_TRAIL_PARTICLES) / NUM_TRAIL_PARTICLES;
        Vec3d pos = start.lerp(destination, delta);
        this.getWorld()
                .addParticle(ModParticleTypes.MIDNIGHT_TRAIL, pos.getX(), pos.getY(), pos.getZ(),
                        direction.getX() * TRAIL_PARTICLE_SPEED,
                        direction.getY() * TRAIL_PARTICLE_SPEED,
                        direction.getZ() * TRAIL_PARTICLE_SPEED);
    }

    // Luhel Bond
    private void drainWaterFromHost(PlayerEntity host) {
        ThirstManagerComponent thirstManager = ModComponents.THIRST_MANAGER.get(host);
        int currentWaterLevel = thirstManager.get();
        MidnightAetherBondComponent bondData = ModComponents.MIDNIGHT_AETHER_BOND.get(host);
        if (currentWaterLevel <= 0) {
            bondData.removeBond(this.getId());
            resetController();
        } else {
            thirstManager.remove(1);
            bondData.updateBond(this.getId());
        }
    }

    public void forgetAboutPlayer(PlayerEntity player) {
        UUID uuid = player.getUuid();
        if (uuid.equals(getControllerUuid())) {
            resetController();
        } else {
            waterBribes.removeInt(uuid);
        }
    }

    public void setController(PlayerEntity player) {
        UUID newControllerUuid = player.getUuid();
        UUID currentControllerUuid = getControllerUuid();
        if (currentControllerUuid != null) {
            if (currentControllerUuid.equals(newControllerUuid)) {
                return;
            } else {
                PlayerEntity formerController = getController();
                if (formerController != null) {
                    ModComponents.MIDNIGHT_AETHER_BOND.get(formerController)
                            .removeBond(this.getId());
                }
            }
        }

        setControllerUuid(newControllerUuid);
        this.controlLevel = ControlLevel.NORMAL;

        // Immediately drain water from host, which will send updates
        drainWaterFromHost(player);
    }

    public void resetController() {
        UUID controllerUuid = getControllerUuid();
        if (controllerUuid != null) {
            waterBribes.removeInt(controllerUuid);
        }
        setControllerUuid(null);
        this.controlLevel = ControlLevel.OUT_OF_CONTROL;
    }

    public void acceptWaterBribe(PlayerEntity player, int waterAmount) {
        UUID uuid = player.getUuid();
        int currentBribe = waterBribes.computeIfAbsent(uuid, id -> 0);
        waterBribes.put(uuid, currentBribe + waterAmount);
        Entry<UUID> entry = Collections.max(waterBribes.object2IntEntrySet(),
                Map.Entry.comparingByValue());
        if (!entry.getKey().equals(getControllerUuid())) {
            setController(player);
        }
    }

    // Shapeshifting Logic
    @Override
    public void onMorphEntitySpawn(LivingEntity morph) {
        super.onMorphEntitySpawn(morph);
        ((MidnightOverlayAccess) morph).worldsinger$setMidnightOverlay(true);
        if (morph instanceof PufferfishEntity pufferfishEntity) {
            pufferfishEntity.setPuffState(PufferfishEntity.FULLY_PUFFED);
        } else if (morph instanceof SchoolingFishEntity) {
            // Don't turn on its side
            ((EntityAccessor) morph).worldsinger$setTouchingWater(true);
        }
    }

    @Override
    public void afterMorphEntitySpawn(LivingEntity morph, boolean showTransformEffects) {
        super.afterMorphEntitySpawn(morph, showTransformEffects);
        if (showTransformEffects && this.getWorld().isClient()) {
            MidnightCreatureEntity.spawnMidnightParticles(this.getWorld(), this, random, 0.2,
                    NUM_TRANSFORM_PARTICLES);
            this.getWorld()
                    .playSoundFromEntity(this, ModSoundEvents.ENTITY_MIDNIGHT_CREATURE_TRANSFORM,
                            this.getSoundCategory(), 2.0f, 1.0f);
        }

        updateStats(morph, showTransformEffects);
    }

    private void updateStats(LivingEntity morph, boolean showTransformEffects) {
        EntityAttributeInstance movementSpeedAttribute = this.getAttributeInstance(
                EntityAttributes.GENERIC_MOVEMENT_SPEED);
        EntityAttributeInstance knockbackResistanceAttribute = this.getAttributeInstance(
                EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
        EntityAttributeInstance maxHealthAttribute = this.getAttributeInstance(
                EntityAttributes.GENERIC_MAX_HEALTH);
        EntityAttributeInstance attackDamageAttribute = this.getAttributeInstance(
                EntityAttributes.GENERIC_ATTACK_DAMAGE);
        Objects.requireNonNull(movementSpeedAttribute);
        Objects.requireNonNull(maxHealthAttribute);
        Objects.requireNonNull(attackDamageAttribute);
        Objects.requireNonNull(knockbackResistanceAttribute);

        if (morph == null) {
            movementSpeedAttribute.setBaseValue(0.0);
            knockbackResistanceAttribute.setBaseValue(1.0);
            maxHealthAttribute.setBaseValue(DEFAULT_MAX_HEALTH);
            attackDamageAttribute.setBaseValue(0.0);
            return;
        }
        float volume = EntityUtil.getSize(morph);
        double maxHealth = MidnightCreatureEntity.getMaxHealthForVolume(volume);
        double attackDamage = MidnightCreatureEntity.getAttackDamageForVolume(volume);
        // Speed is the same for all mobs
        movementSpeedAttribute.setBaseValue(DEFAULT_MOVEMENT_SPEED);
        knockbackResistanceAttribute.setBaseValue(0.0);
        maxHealthAttribute.setBaseValue(maxHealth);
        attackDamageAttribute.setBaseValue(attackDamage);

        if (showTransformEffects) {
            this.setHealth(this.getMaxHealth());
        }
    }

    private void imitateNearestEntity() {
        LivingEntity nearest = getNearestEntityToImitate();
        if (nearest != null) {
            ShapeshiftingManager.createMorphFromEntity(this, nearest, true);
        }
    }

    private LivingEntity getNearestEntityToImitate() {
        Vec3d pos = this.getPos();
        Box box = BoxUtil.createBoxAroundPos(pos, 32.0);

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

    // Overrides
    @Override
    public void onDamaged(DamageSource damageSource) {
        super.onDamaged(damageSource);
        MidnightCreatureEntity.spawnMidnightParticles(this.getWorld(), this, random, 0.25,
                MidnightCreatureEntity.NUM_DAMAGE_PARTICLES);
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getMorph() != null) {
            nbt.putInt(MIDNIGHT_ESSENCE_AMOUNT_KEY, midnightEssenceAmount);
        }
        UUID controllerUuid = getControllerUuid();
        if (controllerUuid != null) {
            nbt.putUuid(CONTROLLER_KEY, controllerUuid);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (this.getMorph() != null) {
            this.midnightEssenceAmount = nbt.getInt(MIDNIGHT_ESSENCE_AMOUNT_KEY);
        }
        if (nbt.contains(CONTROLLER_KEY)) {
            UUID controllerUuid = nbt.getUuid(CONTROLLER_KEY);
            setControllerUuid(controllerUuid);
            PlayerEntity player = getController();
            if (player != null) {
                setController(player);
            }
        }
    }

    public void setMidnightEssenceAmount(int midnightEssenceAmount) {
        this.midnightEssenceAmount = midnightEssenceAmount;
    }

    // Hacky way of disabling certain status effects. Clashes a bit with the entity tag for spore effects, etc.
    // TODO: Investigate better solutions
    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        RegistryEntry<StatusEffect> effectType = effect.getEffectType();
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

    @Override
    protected boolean isDisallowedInPeaceful() {
        return true;
    }

    /* Should act like a hostile mob though it does not extend HostileEntity */

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

    public int getMidnightEssenceAmount() {
        return midnightEssenceAmount;
    }

    public enum ControlLevel {
        OUT_OF_CONTROL, NORMAL, CAN_POSSESS
    }
}
