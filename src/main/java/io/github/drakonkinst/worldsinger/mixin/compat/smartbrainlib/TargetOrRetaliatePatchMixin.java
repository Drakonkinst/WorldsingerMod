package io.github.drakonkinst.worldsinger.mixin.compat.smartbrainlib;

import java.util.function.BiPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRetaliateTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin({ TargetOrRetaliate.class, SetRetaliateTarget.class })
public abstract class TargetOrRetaliatePatchMixin<E extends MobEntity> extends
        ExtendedBehaviour<E> {

    @Shadow
    protected LivingEntity toTarget;

    @Shadow
    protected BiPredicate<E, LivingEntity> allyPredicate;

    /**
     * @author Drakonkinst
     * @reason Temporary fix to patch to 1.20.5 snapshots
     */
    @Overwrite
    protected void alertAllies(ServerWorld level, E owner) {
        double followRange = owner.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);

        for (LivingEntity ally : EntityRetrievalUtil.<LivingEntity>getEntities(level,
                owner.getBoundingBox().expand(followRange, 10, followRange),
                entity -> entity != owner && entity instanceof LivingEntity livingEntity
                        && this.allyPredicate.test(owner, livingEntity))) {
            BrainUtils.setTargetOfEntity(ally, this.toTarget);
        }
    }
}
