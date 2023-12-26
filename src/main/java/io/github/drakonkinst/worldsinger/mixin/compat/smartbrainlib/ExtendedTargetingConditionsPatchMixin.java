package io.github.drakonkinst.worldsinger.mixin.compat.smartbrainlib;

import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.tslat.smartbrainlib.object.ExtendedTargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ExtendedTargetingConditions.class)
public abstract class ExtendedTargetingConditionsPatchMixin {

    @Shadow
    public abstract ExtendedTargetingConditions withRange(Function<LivingEntity, Double> function);

    /**
     * @author Drakonkinst
     * @reason Temporary fix to patch to 1.20.5 snapshots
     */
    @Overwrite
    public ExtendedTargetingConditions withFollowRange() {
        return this.withRange(
                entity -> entity.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE) != null
                        ? entity.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE) : 16d);
    }
}
