package io.github.drakonkinst.worldsinger.util;

import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.mixin.accessor.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;

public final class EntityUtil {

    public static boolean isTouchingSporeSea(Entity entity) {
        return EntityUtil.isTouchingFluid(entity, ModFluidTags.AETHER_SPORES);
    }

    public static boolean isTouchingFluid(Entity entity, TagKey<Fluid> fluidTag) {
        return EntityUtil.notFirstUpdate(entity) && entity.getFluidHeight(fluidTag) > 0.0;
    }

    private static boolean notFirstUpdate(Entity entity) {
        return !((EntityAccessor) entity).isFirstUpdate();
    }

    public static boolean isSubmergedInSporeSea(Entity entity) {
        return EntityUtil.isSubmergedInFluid(entity, ModFluidTags.AETHER_SPORES);
    }

    public static boolean isSubmergedInFluid(Entity entity, TagKey<Fluid> fluidTag) {
        return EntityUtil.notFirstUpdate(entity) && entity.isSubmergedIn(fluidTag);
    }

    private EntityUtil() {}
}
