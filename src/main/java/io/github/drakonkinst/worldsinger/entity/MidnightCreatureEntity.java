package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.component.ModComponents;
import io.github.drakonkinst.worldsinger.component.ThirstManagerComponent;
import io.github.drakonkinst.worldsinger.cosmere.ShapeshiftingManager;
import io.github.drakonkinst.worldsinger.effect.ModStatusEffects;
import io.github.drakonkinst.worldsinger.mixin.accessor.EntityAccessor;
import io.github.drakonkinst.worldsinger.particle.ModParticleTypes;
import io.github.drakonkinst.worldsinger.registry.ModSoundEvents;
import io.github.drakonkinst.worldsinger.util.BoxUtil;
import io.github.drakonkinst.worldsinger.util.EntityUtil;
import io.github.drakonkinst.worldsinger.util.ModEnums.PathNodeType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import org.jetbrains.annotations.Nullable;

public class MidnightCreatureEntity extends ShapeshiftingEntity {

    // Tracked Data
    private static final TrackedData<Optional<UUID>> CONTROLLER_UUID = DataTracker.registerData(
            MidnightCreatureEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    // NBT
    public static final String MIDNIGHT_ESSENCE_AMOUNT_KEY = "MidnightEssenceAmount";
    public static final String CONTROLLER_KEY = "Controller";

    private static final String MORPHED_NAME_TRANSLATION_KEY = Util.createTranslationKey("entity",
            Worldsinger.id("midnight_creature.morphed"));

    // Behavior
    private static final int IMITATE_NEAREST_INTERVAL = 20 * 5;
    private static final int DRAIN_INTERVAL_TICKS = 20 * 3;
    private static final Set<RegistryEntry<StatusEffect>> IMMUNE_TO = Set.of(StatusEffects.WITHER,
            StatusEffects.POISON, StatusEffects.HUNGER, ModStatusEffects.CRIMSON_SPORES,
            ModStatusEffects.MIDNIGHT_SPORES, ModStatusEffects.ROSEITE_SPORES,
            ModStatusEffects.SUNLIGHT_SPORES, ModStatusEffects.VERDANT_SPORES,
            ModStatusEffects.ZEPHYR_SPORES);

    // Attributes
    private static final double DEFAULT_MOVEMENT_SPEED = 0.25;
    private static final double DEFAULT_ATTACK_DAMAGE = 3.0;
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

    public enum ControlLevel {
        OUT_OF_CONTROL, NORMAL, CAN_POSSESS
    }

    public static DefaultAttributeContainer.Builder createMidnightCreatureAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, DEFAULT_ATTACK_DAMAGE)
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

    // Cached controller entity used for client rendering, kept in sync with controller UUID
    @Nullable
    private PlayerEntity clientController;

    public MidnightCreatureEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;

        // Set to same penalty as water
        this.setPathfindingPenalty(PathNodeType.AETHER_SPORE_SEA, 8.0F);
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

    private void setControllerUuid(UUID uuid) {
        if (uuid == null) {
            this.dataTracker.set(CONTROLLER_UUID, Optional.empty());
        } else {
            this.dataTracker.set(CONTROLLER_UUID, Optional.of(uuid));
        }
    }

    private UUID getControllerUuid() {
        return this.dataTracker.get(CONTROLLER_UUID).orElse(null);
    }

    // AI
    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.4, true));
        this.goalSelector.add(3, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge());
        this.targetSelector.add(4,
                new UncontrolledActiveTargetGoal<>(this, LivingEntity.class, true,
                        entity -> !entity.getType().isIn(ModEntityTypeTags.SPORES_NEVER_AFFECT)));
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
                    PlayerEntity player = world.getPlayerByUuid(controllerUuid);
                    if (player == null) {
                        resetController();
                    } else {
                        drainWaterFromHost(player);
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
        UUID controllerUuid = getControllerUuid();
        if (clientController == null && controllerUuid != null) {
            clientController = this.getWorld().getPlayerByUuid(controllerUuid);
        } else if (clientController != null) {
            if (controllerUuid == null) {
                clientController = null;
            } else {
                clientController = this.getWorld().getPlayerByUuid(controllerUuid);
            }
        }

        if (clientController != null) {
            Vec3d start = clientController.getEyePos().add(0.0, MOUTH_OFFSET, 0.0);
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
        MidnightAetherBondData bondData = ((MidnightAetherBondAccess) host).worldsinger$getMidnightAetherBondData();
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
                PlayerEntity formerController = this.getWorld().getPlayerByUuid(newControllerUuid);
                if (formerController != null) {
                    ((MidnightAetherBondAccess) formerController).worldsinger$getMidnightAetherBondData()
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
        EntityAttributeInstance maxHealthAttribute = this.getAttributeInstance(
                EntityAttributes.GENERIC_MAX_HEALTH);
        EntityAttributeInstance attackDamageAttribute = this.getAttributeInstance(
                EntityAttributes.GENERIC_ATTACK_DAMAGE);
        Objects.requireNonNull(movementSpeedAttribute);
        Objects.requireNonNull(maxHealthAttribute);
        Objects.requireNonNull(attackDamageAttribute);

        if (morph == null) {
            movementSpeedAttribute.setBaseValue(0.0);
            maxHealthAttribute.setBaseValue(DEFAULT_MAX_HEALTH);
            attackDamageAttribute.setBaseValue(DEFAULT_ATTACK_DAMAGE);
            return;
        }
        float volume = EntityUtil.getSize(morph);
        double maxHealth = MidnightCreatureEntity.getMaxHealthForVolume(volume);
        double attackDamage = MidnightCreatureEntity.getAttackDamageForVolume(volume);
        // Speed is the same for all mobs
        movementSpeedAttribute.setBaseValue(DEFAULT_MOVEMENT_SPEED);
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
            PlayerEntity player = this.getWorld().getPlayerByUuid(controllerUuid);
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

    public int getMidnightEssenceAmount() {
        return midnightEssenceAmount;
    }

    public static class UncontrolledActiveTargetGoal<T extends LivingEntity> extends
            ActiveTargetGoal<T> {

        MidnightCreatureEntity entity;

        public UncontrolledActiveTargetGoal(MidnightCreatureEntity entity, Class<T> targetClass,
                boolean checkVisibility, @Nullable Predicate<LivingEntity> targetPredicate) {
            super(entity, targetClass, 10, checkVisibility, false, targetPredicate);
            this.entity = entity;
        }

        @Override
        public boolean canStart() {
            return entity.getControllerUuid() != null && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return this.targetPredicate != null ? this.targetPredicate.test(this.mob,
                    this.targetEntity) : super.shouldContinue();
        }
    }
}
