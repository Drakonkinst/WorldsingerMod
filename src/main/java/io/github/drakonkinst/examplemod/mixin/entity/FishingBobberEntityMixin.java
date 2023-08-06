package io.github.drakonkinst.examplemod.mixin.entity;

import io.github.drakonkinst.examplemod.fluid.ModFluidTags;
import io.github.drakonkinst.examplemod.weather.LumarSeetheManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.TagKey;
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
}
