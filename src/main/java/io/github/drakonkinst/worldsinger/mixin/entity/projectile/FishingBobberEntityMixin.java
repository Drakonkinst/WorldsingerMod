package io.github.drakonkinst.worldsinger.mixin.entity.projectile;

import io.github.drakonkinst.worldsinger.entity.SporeFluidEntityStateAccess;
import io.github.drakonkinst.worldsinger.fluid.ModFluidTags;
import io.github.drakonkinst.worldsinger.world.lumar.LumarSeetheManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends ProjectileEntity {


    public FishingBobberEntityMixin(
            EntityType<? extends ProjectileEntity> entityType,
            World world) {
        super(entityType, world);
    }

    // Allow fishing bobbers to bob in spore fluid like water
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private boolean isInWaterOrSporeFluid(FluidState instance, TagKey<Fluid> fluidTag) {
        if (instance.isIn(ModFluidTags.AETHER_SPORES) && LumarSeetheManager.areSporesFluidized(
                this.getWorld())) {
            return true;
        }
        return instance.isIn(fluidTag);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"))
    private void doNotSinkInSolidSpores(FishingBobberEntity instance, MovementType movementType,
            Vec3d vec3d) {
        if (((SporeFluidEntityStateAccess) this).worldsinger$isTouchingSporeSea()
                && !LumarSeetheManager.areSporesFluidized(this.getWorld())) {
            return;
        }
        instance.move(movementType, vec3d);
    }
}
