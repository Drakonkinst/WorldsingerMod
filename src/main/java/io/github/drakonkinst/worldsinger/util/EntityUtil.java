package io.github.drakonkinst.worldsinger.util;

import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.mixin.accessor.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public final class EntityUtil {

    public static boolean isTouchingSporeSea(Entity entity) {
        return EntityUtil.isTouchingFluid(entity, ModFluidTags.AETHER_SPORES);
    }

    public static boolean isTouchingFluid(Entity entity, TagKey<Fluid> fluidTag) {
        return EntityUtil.notFirstUpdate(entity) && entity.getFluidHeight(fluidTag) > 0.0;
    }

    private static boolean notFirstUpdate(Entity entity) {
        return !((EntityAccessor) entity).worldsinger$isFirstUpdate();
    }

    public static boolean isSubmergedInSporeSea(Entity entity) {
        return EntityUtil.isSubmergedInFluid(entity, ModFluidTags.AETHER_SPORES);
    }

    public static boolean isSubmergedInFluid(Entity entity, TagKey<Fluid> fluidTag) {
        return EntityUtil.notFirstUpdate(entity) && entity.isSubmergedIn(fluidTag);
    }

    public static Vec3d getRandomPointInBoundingBox(Entity entity, Random random) {
        Box box = entity.getBoundingBox();
        double x = box.minX + (box.maxX - box.minX) * random.nextDouble();
        double y = box.minY + (box.maxY - box.minY) * random.nextDouble();
        double z = box.minZ + (box.maxZ - box.minZ) * random.nextDouble();
        return new Vec3d(x, y, z);
    }

    private EntityUtil() {}
}
