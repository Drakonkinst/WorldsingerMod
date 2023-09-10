package io.github.drakonkinst.worldsinger.mixin.entity;

import io.github.drakonkinst.worldsinger.entity.ThrownSporeBottleEntity;
import io.github.drakonkinst.worldsinger.util.math.ExtendedRaycastContext;
import io.github.drakonkinst.worldsinger.util.math.ExtendedRaycastContext.ExtendedFluidHandling;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {

    @Redirect(method = "getCollision(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/entity/Entity;Ljava/util/function/Predicate;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/World;)Lnet/minecraft/util/hit/HitResult;", at = @At(value = "NEW", target = "(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/world/RaycastContext$ShapeType;Lnet/minecraft/world/RaycastContext$FluidHandling;Lnet/minecraft/entity/Entity;)Lnet/minecraft/world/RaycastContext;"))
    private static RaycastContext makeThrownPotionsBreakAgainstSporeSea(Vec3d start, Vec3d end,
            ShapeType shapeType, FluidHandling fluidHandling, Entity entity) {
        // Make potions break upon hitting spore sea instead of falling through
        if (entity instanceof PotionEntity || entity instanceof ThrownSporeBottleEntity) {
            return new ExtendedRaycastContext(start, end, shapeType,
                    ExtendedFluidHandling.SPORE_SEA, entity);
        }
        // Original
        return new RaycastContext(start, end, shapeType, fluidHandling, entity);
    }
}
