package io.github.drakonkinst.worldsinger.util;

import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.mixin.accessor.EntityAccessor;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

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

    // Used as a metric of how big an entity is, used for a variety of size-based
    // calculations. Volume is not a great value here, as it is cubic.
    public static float getSize(Entity entity) {
        return entity.getWidth() * entity.getHeight();
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

    @NotNull
    public static EntityAttributeInstance getRequiredAttributeInstance(LivingEntity entity,
            RegistryEntry<EntityAttribute> attribute) {
        EntityAttributeInstance instance = entity.getAttributeInstance(attribute);
        Objects.requireNonNull(instance);
        return instance;
    }

    private EntityUtil() {}
}
