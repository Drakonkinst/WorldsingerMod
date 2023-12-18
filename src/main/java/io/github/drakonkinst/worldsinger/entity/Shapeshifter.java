package io.github.drakonkinst.worldsinger.entity;

import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface Shapeshifter {

    void setMorph(@Nullable LivingEntity morph);

    // Should implicitly call setMorph()
    void updateMorph(@Nullable LivingEntity morph);

    default void onMorphEntitySpawn(LivingEntity morph) {
        // Do nothing
    }

    // showTransformEffects = whether the entity has just spawned. Used to determine whether particles should spawn or not.
    default void afterMorphEntitySpawn(LivingEntity morph, boolean showTransformEffects) {
        // Do nothing
    }

    default boolean shouldCopyEquipmentVisuals() {
        return false;
    }

    default boolean shouldRenderNameTag() {
        return true;
    }

    @Nullable LivingEntity getMorph();

    LivingEntity toEntity();
}
