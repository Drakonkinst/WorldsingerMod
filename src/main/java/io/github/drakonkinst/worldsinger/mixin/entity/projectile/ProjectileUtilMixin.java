package io.github.drakonkinst.worldsinger.mixin.entity.projectile;

import io.github.drakonkinst.worldsinger.util.math.ExtendedRaycastContext;
import io.github.drakonkinst.worldsinger.util.math.ExtendedRaycastContext.ExtendedFluidHandling;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeetheManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
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
    private static RaycastContext modifyRaycastContext(Vec3d start, Vec3d end,
            ShapeType shapeType,
            FluidHandling fluidHandling, Entity entity) {
        if (LumarSeetheManager.areSporesFluidized(entity.getWorld())) {
            // Default
            return new RaycastContext(start, end, shapeType, fluidHandling, entity);
        } else {
            // Collide with spore sea
            return new ExtendedRaycastContext(start, end, shapeType,
                    ExtendedFluidHandling.SPORE_SEA, entity);
        }
    }

}
