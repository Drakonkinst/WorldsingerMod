package io.github.drakonkinst.examplemod.mixin;

import io.github.drakonkinst.examplemod.fluid.ModFluidTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class WaterWalkMixin extends Entity {

    public WaterWalkMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "canWalkOnFluid", at = @At("HEAD"), cancellable = true)
    private void allowWalkingOnSporesDuringRain(FluidState state,
            CallbackInfoReturnable<Boolean> cir) {
        // if (state.isIn(ModFluidTags.STILL_AETHER_SPORES) && getWorld().isRaining()) {
        if (state.isIn(ModFluidTags.AETHER_SPORES) && getWorld().isRaining()) {
            cir.setReturnValue(true);
        }
    }
}
