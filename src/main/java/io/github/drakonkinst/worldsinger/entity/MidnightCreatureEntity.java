package io.github.drakonkinst.worldsinger.entity;

import io.github.drakonkinst.worldsinger.Worldsinger;
import io.github.drakonkinst.worldsinger.util.BoxUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MidnightCreatureEntity extends ShapeshiftingEntity {

    private static final int TICK_INTERVAL = 20 * 5;

    public static DefaultAttributeContainer.Builder createMidnightCreatureAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    public MidnightCreatureEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (age % TICK_INTERVAL == 0) {
            Vec3d pos = this.getPos();
            HostileEntity nearest = this.getWorld()
                    .getClosestEntity(HostileEntity.class, TargetPredicate.DEFAULT, this,
                            pos.getX(), pos.getY(), pos.getZ(),
                            BoxUtil.createBoxAroundPos(pos.getX(), pos.getY(), pos.getZ(), 16.0));
            if (nearest != null) {
                Worldsinger.LOGGER.info(nearest.getName().toString());
                HostileEntity copy = (HostileEntity) nearest.getType().create(this.getWorld());
                this.updateIdentity(copy);
            } else {
                Worldsinger.LOGGER.info("NO MOB FOUND");
                this.updateIdentity(null);
            }
        }
    }
}
