package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

// Might reuse this code for other creatures that can shapeshift later.
public abstract class ShapeshiftingEntity extends PathAwareEntity implements Shapeshifter {

    protected LivingEntity identity;

    protected ShapeshiftingEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setIdentity(@Nullable LivingEntity identity) {
        if (identity != null) {
            Worldsinger.LOGGER.info("SETTING IDENTITY TO " + identity.getName().toString());
        } else {
            Worldsinger.LOGGER.info("CLEARING IDENTITY");
        }
        this.identity = identity;
    }

    public void updateIdentity(@Nullable LivingEntity identity) {
        Worldsinger.LOGGER.info("B");
        Worldsinger.LOGGER.info((this.getEyePos().subtract(this.getPos())).toString());
        this.setIdentity(identity);
        this.calculateDimensions();

        World world = this.getWorld();
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            Shapeshifter.sync(serverWorld, this);
        }
    }

    @Nullable
    public LivingEntity getIdentity() {
        return identity;
    }

    @Override
    public LivingEntity toEntity() {
        return this;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        if (identity == null) {
            return super.getDimensions(pose);
        }
        return identity.getDimensions(pose);
    }

    @Override
    public float getEyeHeight(EntityPose pose) {
        if (identity == null) {
            return super.getEyeHeight(pose);
        }
        return identity.getEyeHeight(pose);
    }
}
