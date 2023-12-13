package io.github.drakonkinst.worldsinger.entity;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

// Might reuse this code for other creatures that can shapeshift later.
// Note that even non-player entities might not extend this class; that's why it's important to use
// the Shapeshifter interface where possible. This is just one example of how this can be saved
// and loaded.
public abstract class ShapeshiftingEntity extends PathAwareEntity implements Shapeshifter {

    protected static final TrackedData<NbtCompound> MORPH = DataTracker.registerData(
            ShapeshiftingEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);

    public static final String MORPH_KEY = "Morph";

    protected LivingEntity morph = null;
    private boolean hasLoadedMorph = false;

    protected ShapeshiftingEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        // When initially loaded on the client side, sync it with the data
        if (this.getWorld().isClient() && !hasLoadedMorph) {
            checkMorphOnLoad();
            hasLoadedMorph = true;
        }
    }

    private void checkMorphOnLoad() {
        NbtCompound morphData = this.getMorphData();
        if (morphData.isEmpty() && morph != null) {
            this.updateMorph(null);
        } else if (!morphData.isEmpty()) {
            if (morph == null) {
                Shapeshifter.createEntityFromNbt(this, morphData);
            } else {
                UUID uuid = morphData.getUuid(Entity.UUID_KEY);
                if (!morph.getUuid().equals(uuid)) {
                    Shapeshifter.createEntityFromNbt(this, morphData);
                }
            }
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(MORPH, new NbtCompound());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put(MORPH_KEY, this.getMorphData());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setMorphData(nbt.getCompound(MORPH_KEY));
        this.setMorphFromData();
    }

    public void setMorph(@Nullable LivingEntity morph) {
        this.morph = morph;
    }

    private void setMorphDataFromEntity(LivingEntity morph) {
        NbtCompound nbtCompound = new NbtCompound();
        if (morph != null) {
            morph.saveSelfNbt(nbtCompound);
        }
        this.setMorphData(nbtCompound);
    }

    private void setMorphFromData() {
        NbtCompound morphNbt = this.getMorphData();
        Shapeshifter.createEntityFromNbt(this, morphNbt);
    }

    private void setMorphData(NbtCompound nbtCompound) {
        this.dataTracker.set(MORPH, nbtCompound);
    }

    public NbtCompound getMorphData() {
        return this.dataTracker.get(MORPH);
    }

    public void updateMorph(@Nullable LivingEntity morph) {
        this.setMorph(morph);
        this.calculateDimensions();

        World world = this.getWorld();
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            this.setMorphDataFromEntity(morph);
            Shapeshifter.syncToNearbyPlayers(serverWorld, this);
        }
    }

    @Nullable
    public LivingEntity getMorph() {
        return morph;
    }

    @Override
    public LivingEntity toEntity() {
        return this;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        if (morph == null) {
            return super.getDimensions(pose);
        }
        return morph.getDimensions(pose);
    }

    @Override
    public float getEyeHeight(EntityPose pose) {
        if (morph == null) {
            return super.getEyeHeight(pose);
        }
        return morph.getEyeHeight(pose);
    }
}
