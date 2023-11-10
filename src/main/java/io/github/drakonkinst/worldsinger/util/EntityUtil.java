package io.github.drakonkinst.worldsinger.util;

import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.mixin.accessor.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;

public final class EntityUtil {

    private static boolean isFirstUpdate(Entity entity) {
        return ((EntityAccessor) entity).isFirstUpdate();
    }

    public static boolean isTouchingFluid(Entity entity, TagKey<Fluid> fluidTag) {
        return !isFirstUpdate(entity) && entity.getFluidHeight(fluidTag) > 0.0;
    }

    public static boolean isSubmergedInFluid(Entity entity, TagKey<Fluid> fluidTag) {
        return !isFirstUpdate(entity) && entity.isSubmergedIn(fluidTag);
    }

    public static boolean isTouchingSporeSea(Entity entity) {
        return isTouchingFluid(entity, ModFluidTags.AETHER_SPORES);
    }

    public static boolean isSubmergedInSporeSea(Entity entity) {
        return isSubmergedInFluid(entity, ModFluidTags.AETHER_SPORES);
    }

    private EntityUtil() {}
}
