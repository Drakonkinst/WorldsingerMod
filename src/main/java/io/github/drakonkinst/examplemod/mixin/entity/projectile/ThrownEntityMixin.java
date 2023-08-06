package io.github.drakonkinst.examplemod.mixin.entity.projectile;

import io.github.drakonkinst.examplemod.util.math.ExtendedRaycastContext;
import io.github.drakonkinst.examplemod.util.math.ExtendedRaycastContext.ExtendedFluidHandling;
import io.github.drakonkinst.examplemod.weather.LumarSeetheManager;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThrownEntity.class)
public abstract class ThrownEntityMixin extends ProjectileEntity {

    public ThrownEntityMixin(EntityType<? extends ProjectileEntity> entityType,
            World world) {
        super(entityType, world);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;getCollision(Lnet/minecraft/entity/Entity;Ljava/util/function/Predicate;)Lnet/minecraft/util/hit/HitResult;"))
    private HitResult injectRaycastLogic(Entity entity,
            Predicate<Entity> canHit) {
        if (LumarSeetheManager.areSporesFluidized(this.getWorld())) {
            // Default
            return ProjectileUtil.getCollision(entity, canHit);
        } else {
            return getCollisionIncludingSporeFluid(entity, canHit);
        }
    }

    @Unique
    private HitResult getCollisionIncludingSporeFluid(Entity entity, Predicate<Entity> canHit) {
        Vec3d velocity = entity.getVelocity();
        World world = entity.getWorld();
        Vec3d pos = entity.getPos();

        EntityHitResult hitResult2;
        Vec3d vec3d = pos.add(velocity);
        HitResult hitResult = world.raycast(
                new ExtendedRaycastContext(pos, vec3d, RaycastContext.ShapeType.COLLIDER,
                        ExtendedFluidHandling.SPORE_SEA, entity));

        if (((HitResult) hitResult).getType() != HitResult.Type.MISS) {
            vec3d = hitResult.getPos();
        }
        if ((hitResult2 = ProjectileUtil.getEntityCollision(world, entity, pos, vec3d,
                entity.getBoundingBox().stretch(velocity).expand(1.0), canHit)) != null) {
            hitResult = hitResult2;
        }
        return hitResult;
    }

}
